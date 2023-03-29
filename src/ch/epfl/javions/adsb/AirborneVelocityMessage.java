package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;
import java.util.Objects;
import static ch.epfl.javions.Units.Speed.KNOT;

public record AirborneVelocityMessage (long timeStampNs, IcaoAddress icaoAddress,
                                       double speed, double trackOrHeading) implements Message {
    /**
     * Compact constructor for AirborneVelocityMessage
     * @param timeStampNs timestamp
     * @param icaoAddress icaoAddress
     * @param speed speed of the airplane
     * @param trackOrHeading direction of the airplane
     */
    public AirborneVelocityMessage{
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument((timeStampNs >= 0) && (speed >= 0) && (trackOrHeading >= 0));
    }

    /**
     * Of method for AirborneVelocityMessage
     * Creates an instance of an AirborneVelocityMessage from the
     * rawMessage which returns (timestamp, ICAO, velocity, direction)
     * @param rawMessage rawMessage
     * @return instance of AirborneVelocityMessage
     */
    public static AirborneVelocityMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();
        long timeStampNs = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        int subtype = Bits.extractUInt(payload,48,3);
        long contentOfMessage = Bits.extractUInt(payload,21,22);


        //different cases of subtype

        switch (subtype) {
            case 1, 2 -> {
                //direction
                int Dew = Bits.extractUInt(contentOfMessage, 21, 1);
                int Dns = Bits.extractUInt(contentOfMessage, 10, 1);
                //velocity
                int Vew = Bits.extractUInt(contentOfMessage, 11, 10) - 1;
                int Vns = Bits.extractUInt(contentOfMessage, 0, 10) - 1;
                if (Vew == -1 || Vns == -1) return null;
                //speed
                double vel = Math.hypot(Vew, Vns);
                vel = Units.convertFrom(vel, KNOT);
                //angle
                int x = (Dew == 0) ? Vew : -Vew;
                int y = (Dns == 0) ? Vns : -Vns;
                double dir = Math.atan2(x, y);
                dir = (dir < 0) ? (2 * Math.PI) + dir : dir;
                //dir = Units.convertTo(dir, DEGREE);
                //adjusting for case
                if (subtype == 2) vel *= 4;
                return new AirborneVelocityMessage(timeStampNs,icaoAddress,vel,dir);
            }
            case 3, 4 -> {
                int headingAvailable = Bits.extractUInt(contentOfMessage, 21, 1);
                //direction
                double heading;
                if (headingAvailable == 1) {
                    heading = (Bits.extractUInt(contentOfMessage, 11, 10) / Math.pow(2, 10));
                    //heading = Units.convertTo(heading, DEGREE);
                }
                else {return null;}

                //speed
                double airspeed = Bits.extractUInt(contentOfMessage, 0, 10) - 1;
                if (airspeed == -1) return null;
                //adjusting for case
                airspeed = (subtype == 3) ? airspeed : airspeed * 4;
                airspeed = Units.convertFrom(airspeed, KNOT);
                return new AirborneVelocityMessage(timeStampNs,icaoAddress,airspeed,heading);
            }
            default -> {
                return null;
            }
        }

    }
}
