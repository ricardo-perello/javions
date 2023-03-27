package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import static java.util.Objects.requireNonNull;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress,
                                      double speed, double trackOrHeading) {


    public AirborneVelocityMessage{
        requireNonNull(icaoAddress);
        Preconditions.checkArgument(timeStampNs>= 0);
        Preconditions.checkArgument(speed >= 0);
        Preconditions.checkArgument(trackOrHeading >= 0);
    }

    public static AirbornePositionMessage of(RawMessage rawMessage){



        long rawMessageMe = rawMessage.payload();
        long rawMessageSpeed = Bits.extractUInt(rawMessageMe, 21,22);
        int type = Bits.extractUInt(rawMessageMe, 48, 3);
        double trackOrHeading = 0;
        double speedNOTTS = speedComparedToGround(type, trackOrHeading, rawMessageSpeed);
    }



    private static double speedComparedToGround(int type, double trackOrHeading, long rawMessageSpeed){


        if(type == 1 ||type == 2){
            int Dew = Bits.extractUInt(rawMessageSpeed, 21, 1);
            int Vew = Bits.extractUInt(rawMessageSpeed, 11,10) - 1;
            int Dns = Bits.extractUInt(rawMessageSpeed, 10,1);
            int Vns = Bits.extractUInt(rawMessageSpeed, 0, 10) - 1;
            //TODO encontrar formula para el angulo
            return (type == 1) ? Math.hypot(Vew,Vns) : 4;
        } else{

            int SH = Bits.extractUInt(rawMessageSpeed, 21 ,1);
            int HDG = Bits.extractUInt(rawMessageSpeed, 11,10); //unsigned int ???????
            int AS = Bits.extractUInt(rawMessageSpeed,0,10) - 1;

            return(type == 3) ? AS : 4;

        }


    }
}
