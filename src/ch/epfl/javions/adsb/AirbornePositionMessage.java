package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import static java.util.Objects.requireNonNull;

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress,
                                      double altitude, int parity, double x, double y) implements Message {

    private static final double NORMALIZER = Math.pow(2, -17);
    private static final int[] POSITIONS = {4, 2, 0, 10, 8, 6, 5, 3, 1, 11, 9, 7};

    /**
     * constructor for AirbornePositionMessage
     *
     * @param timeStampNs long, the timestamp of the message, in nanoseconds, must be non-negative
     * @param icaoAddress IcaoAdress, ICAO address of the sender of the message, must be non-null
     * @param altitude,   double,  the altitude at which the aircraft was at the time the message was sent, in meters
     * @param parity,     int, parity of the message (0 if even, 1 if odd)
     * @param x           double, longitude of plane, (0<=x<1)
     * @param y           double, latitude of plane, (0<=y<1)
     */
    public AirbornePositionMessage {
        requireNonNull(icaoAddress);
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument((parity == 0) || (parity == 1));
        Preconditions.checkArgument(x >= 0 && x < 1);
        Preconditions.checkArgument(y >= 0 && y < 1);
    }

    /**
     * public method of() that allows us to find the position of the plane
     *
     * @param rawMessage RawMessage, message sent by the plane, we decode it so that we can find the important information
     * @return either null, if the altitude is NaN (look at altitudeFinder() for conditions), or a new AirbornePositionMessage
     * using the timeStampNs, icaoAddress, altitude, parity, longitude, latitude we found from the rawMessage
     */
    public static AirbornePositionMessage of(RawMessage rawMessage) {
        long rawMessageME = rawMessage.payload();
        double altitude = altitudeFinder(Bits.extractUInt(rawMessageME, 36, 12));
        if (Double.isNaN(altitude)) {
            return null;
        }
        long timeStampNs = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        int parity = Bits.extractUInt(rawMessageME, 34, 1);
        double latitude = Bits.extractUInt(rawMessageME, 17, 17) * NORMALIZER;
        double longitude = Bits.extractUInt(rawMessageME, 0, 17) * NORMALIZER;
        return new AirbornePositionMessage(timeStampNs, icaoAddress, altitude, parity, longitude, latitude);
    }

    /**
     * private method to calculate the altitude
     *
     * @param rawMessageAltitude long, altitude extracted from the rawMessageMe
     * @return double, NaN if decodedGraySMALLEST == 0,5 or 6; or the altitude
     */
    private static double altitudeFinder(long rawMessageAltitude) {
        boolean qAltitude = Bits.testBit(rawMessageAltitude, 4);
        //if qAltitude == 1
        if (qAltitude) {
            rawMessageAltitude = (long) Bits.extractUInt(rawMessageAltitude, 5, 7) << 4 |
                    Bits.extractUInt(rawMessageAltitude, 0, 4);
            double altitudeFeet = 25 * rawMessageAltitude - 1000;
            return Units.convert(altitudeFeet, Units.Length.FOOT, Units.Length.METER);
        }
        // if qAltitude == 0
        //we reorder rawMessageAltitude
        long sortedAltitude = sortRawMessageAltitude(rawMessageAltitude);

        //we take the first 9
        long sortedAltitudeBIGGEST = Bits.extractUInt(sortedAltitude,3,9);

        //we take the last 3
        long sortedAltitudeSMALLEST = Bits.extractUInt(sortedAltitude,0,3);

        //tranform gray code
        int decodedGrayBIGGEST = decodeGray(sortedAltitudeBIGGEST, 9);
        int decodedGraySMALLEST = decodeGray(sortedAltitudeSMALLEST, 3);

        if (decodedGraySMALLEST == 0 || decodedGraySMALLEST == 5 || decodedGraySMALLEST == 6) {
            return Double.NaN;
        }
        if (decodedGraySMALLEST == 7) {
            decodedGraySMALLEST = 5;
        }
        if (decodedGrayBIGGEST % 2 == 1) {
            decodedGraySMALLEST = 6 - decodedGraySMALLEST;
        }
        double altitudeFEET = decodedGraySMALLEST * 100 + decodedGrayBIGGEST * 500 - 1300;
        return Units.convert(altitudeFEET, Units.Length.FOOT, Units.Length.METER);
    }

    /**
     * method that allows to pass from Gray code to int (Gray code is different to Binary)
     *
     * @param grayCode long, the number we want to decode
     * @param length   int, length of the long
     * @return int, int related to the Gray code
     */
    private static int decodeGray(long grayCode, int length) {
        int decodedGray = 0;
        for (int i = 0; i < length; i++) {
            decodedGray ^= grayCode >> i;
        }
        return decodedGray;
    }

    private static long sortRawMessageAltitude(long rawMessageAltitude) {
        long sortedAltitude = 0;
        for (int i = 0; i < 12; i++) {
                sortedAltitude |= (long) Bits.extractUInt(rawMessageAltitude, POSITIONS[i], 1) << (11-i);
        }
        return sortedAltitude;
    }

}
