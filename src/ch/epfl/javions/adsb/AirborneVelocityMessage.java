package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

import static ch.epfl.javions.Units.Angle.TURN;
import static ch.epfl.javions.Units.Speed.KNOT;

public record AirborneVelocityMessage(long timeStampNs, IcaoAddress icaoAddress,
                                      double speed, double trackOrHeading) implements Message {

    private final static int SUBTYPE_START = 48;
    private final static int SUBTYPE_LENGTH = 3;
    private final static int CONTENT_START = 21;
    private final static int CONTENT_LENGTH = 22;
    private final static int DEW_START = 21;
    private final static int DEW_LENGTH = 1;
    private final static int DNS_START = 10;
    private final static int DNS_LENGTH = 1;
    private final static int VEW_START = 11;
    private final static int VEW_LENGTH = 10;
    private final static int VELOCITY_OFFSET = 1;
    private final static int VNS_START = 0;
    private final static int VNS_LENGTH = 10;
    private final static int HEADING_AVAILABLE_START = 21;
    private final static int HEADING_AVAILABLE_LENGTH = 1;
    private final static int HEADING_START = 11;
    private final static int HEADING_LENGTH = 10;
    private final static double HEADING_MULTIPLIER = Math.pow(2, 10);
    private final static int AIRSPEED_START = 0;
    private final static int AIRSPEED_LENGTH = 10;
    private final static int SUPERSONIC_MULTIPLIER = 4;
    private final static int SUBSONIC_MULTIPLIER = 1;


    /**
     * Compact constructor for AirborneVelocityMessage
     *
     * @param timeStampNs    timestamp
     * @param icaoAddress    icaoAddress
     * @param speed          speed of the airplane
     * @param trackOrHeading direction of the airplane
     */
    public AirborneVelocityMessage {
        Objects.requireNonNull(icaoAddress);
        Preconditions.checkArgument((timeStampNs >= 0) && (speed >= 0) && (trackOrHeading >= 0));
    }

    /**
     * Of method for AirborneVelocityMessage
     * Creates an instance of an AirborneVelocityMessage from the
     * rawMessage which returns (timestamp, ICAO, velocity, direction)
     *
     * @param rawMessage rawMessage
     * @return instance of AirborneVelocityMessage
     */
    public static AirborneVelocityMessage of(RawMessage rawMessage) {
        long payload = rawMessage.payload();
        int subtype = Bits.extractUInt(payload, SUBTYPE_START, SUBTYPE_LENGTH);
        long contentOfMessage = Bits.extractUInt(payload, CONTENT_START, CONTENT_LENGTH);


        //different cases of subtype

        switch (subtype) {
            case 1 -> {
                return groundSpeed(rawMessage, contentOfMessage, SUBSONIC_MULTIPLIER);
            }
            case 2 -> {
                return groundSpeed(rawMessage, contentOfMessage, SUPERSONIC_MULTIPLIER);
            }
            case 3 -> {
                return airSpeed(rawMessage, contentOfMessage, SUBSONIC_MULTIPLIER);
            }
            case 4 -> {
                return airSpeed(rawMessage, contentOfMessage, SUPERSONIC_MULTIPLIER);
            }
            default -> {
                return null;
            }
        }

    }

    /**
     * Ground speed method that is used for subtypes 1 and 2.
     *
     * @param rawMessage       rawMessage
     * @param contentOfMessage useful part of the payload
     * @param vel_multiplier   subsonic or supersonic aircraft
     * @return AirborneVelocityMessage of subtype 1 or 2
     */
    private static AirborneVelocityMessage groundSpeed(RawMessage rawMessage, long contentOfMessage, int vel_multiplier) {
        //direction
        int Dew = Bits.extractUInt(contentOfMessage, DEW_START, DEW_LENGTH);
        int Dns = Bits.extractUInt(contentOfMessage, DNS_START, DNS_LENGTH);
        //velocity
        int Vew = Bits.extractUInt(contentOfMessage, VEW_START, VEW_LENGTH) - VELOCITY_OFFSET;
        int Vns = Bits.extractUInt(contentOfMessage, VNS_START, VNS_LENGTH) - VELOCITY_OFFSET;
        if (Vew == -1 || Vns == -1) return null;
        //speed
        double vel = Math.hypot(Vew, Vns);
        vel = Units.convertFrom(vel, KNOT);
        //angle
        int x = (Dew == 0) ? Vew : -Vew;
        int y = (Dns == 0) ? Vns : -Vns;
        double dir = Math.atan2(x, y);
        dir = (dir < 0) ? (2 * Math.PI) + dir : dir;
        //adjusting for case
        vel *= vel_multiplier;

        return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), vel, dir);
    }

    /**
     * Air speed method that is used for subtypes 1 and 2.
     *
     * @param rawMessage       rawMessage
     * @param contentOfMessage useful part of the payload
     * @param vel_multiplier   subsonic or supersonic aircraft
     * @return AirborneVelocityMessage of subtype 3 or 4
     */
    private static AirborneVelocityMessage airSpeed(RawMessage rawMessage, long contentOfMessage, int vel_multiplier) {
        int headingAvailable = Bits.extractUInt(contentOfMessage, HEADING_AVAILABLE_START, HEADING_AVAILABLE_LENGTH);
        //direction
        double heading;
        if (headingAvailable == 1) {
            heading = (Bits.extractUInt(contentOfMessage, HEADING_START, HEADING_LENGTH) / HEADING_MULTIPLIER);
            heading = Units.convertFrom(heading, TURN);
            double airspeed = Bits.extractUInt(contentOfMessage, AIRSPEED_START, AIRSPEED_LENGTH) - VELOCITY_OFFSET;
            //speed
            if (!(airspeed == -1)) {
                airspeed *= vel_multiplier;
                airspeed = Units.convertFrom(airspeed, KNOT);

                return new AirborneVelocityMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(), airspeed, heading);
            }
        }
        return null;


    }
}
