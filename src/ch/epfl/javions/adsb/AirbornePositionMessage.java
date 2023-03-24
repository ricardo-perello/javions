package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;
import ch.epfl.javions.aircraft.IcaoAddress;

import static java.util.Objects.requireNonNull;

public record AirbornePositionMessage(long timeStampNs, IcaoAddress icaoAddress,
                                      double altitude, int parity, double x, double y) implements Message {

    public AirbornePositionMessage {
        requireNonNull(icaoAddress);
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument((parity == 0) || (parity == 1));
        Preconditions.checkArgument(x >= 0 && x < 1);
        Preconditions.checkArgument(y >= 0 && y < 1);

    }

    public static AirbornePositionMessage of(RawMessage rawMessage) {
        long rawMessageME = rawMessage.payload();
        long timeStampNs = rawMessage.timeStampNs();
        IcaoAddress icaoAddress = rawMessage.icaoAddress();
        double altitude = altitudeFinder(rawMessageME);
        int parity = Bits.extractUInt(rawMessageME, 34, 1);
        double latitude = Bits.extractUInt(rawMessageME, 17, 17) * Math.pow(2, -17);
        double longitude = Bits.extractUInt(rawMessageME, 0, 17) * Math.pow(2, -17);
        if (Double.isNaN(altitude)) {
            return null;
        }
        return new AirbornePositionMessage(timeStampNs, icaoAddress, altitude, parity, longitude, latitude);
    }


    private static double altitudeFinder(long rawMessageME) {
        long rawMessageAltitude = Bits.extractUInt(rawMessageME, 36, 12);
        boolean qAltitude = Bits.testBit(rawMessageAltitude, 4);
        if (qAltitude) {
            rawMessageAltitude = (long) Bits.extractUInt(rawMessageAltitude, 5, 7) << 4 |
                    Bits.extractUInt(rawMessageAltitude, 0, 4);
            double altitudeFeet = 25 * rawMessageAltitude - 1000;
            return Units.convert(altitudeFeet, Units.Length.FOOT, Units.Length.METER);
        } else {
            long rawMessageAltitudeUnraveledBIGGEST = (long) Bits.extractUInt(rawMessageAltitude, 4, 1) << 8 |
                    (long) Bits.extractUInt(rawMessageAltitude, 2, 1) << 7 |
                    (long) Bits.extractUInt(rawMessageAltitude, 0, 1) << 6 |
                    (long) Bits.extractUInt(rawMessageAltitude, 10, 1) << 5 |
                    (long) Bits.extractUInt(rawMessageAltitude, 8, 1) << 4 |
                    (long) Bits.extractUInt(rawMessageAltitude, 6, 1) << 3 |
                    (long) Bits.extractUInt(rawMessageAltitude, 5, 1) << 2 |
                    (long) Bits.extractUInt(rawMessageAltitude, 3, 1) << 1 |
                    (long) Bits.extractUInt(rawMessageAltitude, 1, 1);
            long rawMessageAltitudeUnraveledSMALLEST = (long) Bits.extractUInt(rawMessageAltitude, 11, 1) << 2 |
                    (long) Bits.extractUInt(rawMessageAltitude, 9, 1) << 1 |
                    Bits.extractUInt(rawMessageAltitude, 7, 1);
            int decodedGrayBIGGEST = decodeGray(rawMessageAltitudeUnraveledBIGGEST, 9);
            int decodedGraySMALLEST = decodeGray(rawMessageAltitudeUnraveledSMALLEST, 3);
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
    }

    private static int decodeGray(long grayCode, int lenght) {
        int decodedGray = 0;
        for (int i = 0; i < lenght; i++) {
            decodedGray ^= grayCode >> i;
        }
        return decodedGray;
    }

}
