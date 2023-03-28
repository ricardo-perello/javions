package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

import static ch.epfl.javions.Units.Angle.DEGREE;

public record AirborneVelocityMessage (long timeStampNs, IcaoAddress icaoAddress,
                                       double speed, double trackOrHeading) implements Message {
    public AirborneVelocityMessage{
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument((timeStampNs >= 0) && (speed >= 0) && (trackOrHeading >= 0));
    }

    public static AirborneVelocityMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();
        long timeStampNs = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        int subtype = Bits.extractUInt(payload,48,3);
        long contentOfMessage = Bits.extractUInt(payload,21,22);


        //different cases of subtype

        switch(subtype){
            case 1,2:
                //direction
                int Dew = Bits.extractUInt(contentOfMessage,21,1);
                int Dns = Bits.extractUInt(contentOfMessage,10,1);
                //velocity
                int Vew = Bits.extractUInt(contentOfMessage,11,10) - 1;
                int Vns = Bits.extractUInt(contentOfMessage,0,10) - 1;
                if (Vew == 0 || Vns == 0) return null;
                //speed
                double speed = Math.hypot(Vew, Vns);
                //angle
                int x = (Dew == 0) ? Vew : Vew * -1 ;
                int y = (Dns == 0) ? Vns : Vns * -1 ;
                double trackOrHeading =  Math.atan2(x,y);
                trackOrHeading = (trackOrHeading < 0) ? (2*Math.PI)+trackOrHeading : trackOrHeading;
                trackOrHeading = Units.convertTo(trackOrHeading,DEGREE );
                //adjusting for case
                if (subtype == 2) speed+=4;
                break;
            case 3,4:
               int headingAvailable = Bits.extractUInt(contentOfMessage, 21, 1);
               if (headingAvailable == 1) {
                   double heading = (Bits.extractUInt(contentOfMessage, 11, 10)/Math.pow(2,10));
               }



                break;
            default:
                return null;
                // TODO: 28/3/23 check if this is true
        }
        return new AirborneVelocityMessage(timeStampNs,icaoAddress,speed,trackOrHeading);
    }
}
