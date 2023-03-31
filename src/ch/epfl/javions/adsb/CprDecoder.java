package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder {

    private static final double NUMBER_ZONES_LATITUDE_0 = 60.0;
    private static final double NUMBER_ZONES_LATITUDE_1 = 59.0;
    private static final double WIDTH_ZONES_LATITUDE_0 = 1.0 / NUMBER_ZONES_LATITUDE_0;
    private static final double WIDTH_ZONES_LATITUDE_1 = 1.0 / NUMBER_ZONES_LATITUDE_1;


    /**
     * constructor for CprDecoder
     *
     * @param x0         double, local longitude of an even message
     * @param y0         double, local latitude of an even message
     * @param x1         double, local longitude of an odd message
     * @param y1         double, local latitude of an odd message
     * @param mostRecent int tells us the parity of the message
     * @return GeoPos, the position of the plane
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {

        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);

        double zLatitude = Math.rint(y0 * NUMBER_ZONES_LATITUDE_1 - y1 * NUMBER_ZONES_LATITUDE_0);
        double zLatitude0 = (zLatitude < 0) ? zLatitude + NUMBER_ZONES_LATITUDE_0 : zLatitude;
        double zLatitude1 = (zLatitude < 0) ? zLatitude + NUMBER_ZONES_LATITUDE_1 : zLatitude;
        double latitudeTurn0 = (WIDTH_ZONES_LATITUDE_0 * (zLatitude0 + y0));
        double latitudeTurn1 = (WIDTH_ZONES_LATITUDE_1 * (zLatitude1 + y1));


        double A0 = Math.acos(1 - (1 - Math.cos(2 * Math.PI * WIDTH_ZONES_LATITUDE_0)) /
                Math.pow(Math.cos(Units.convert(latitudeTurn0, Units.Angle.TURN, Units.Angle.RADIAN)), 2));
        double A1 = Math.acos(1 - (1 - Math.cos(2 * Math.PI * WIDTH_ZONES_LATITUDE_0)) /
                Math.pow(Math.cos(Units.convert(latitudeTurn1, Units.Angle.TURN, Units.Angle.RADIAN)), 2));
        double zA0 = Math.floor((2 * Math.PI) / A0);
        double zA1 = Math.floor((2 * Math.PI) / A1);
        if (zA1 != zA0 && !Double.isNaN(A0) && !Double.isNaN(A1)) {
            return null;
        }


        latitudeTurn0 = turnConvert(latitudeTurn0);
        latitudeTurn1 = turnConvert(latitudeTurn1);
        int latitude0T32 = (int) Math.rint(convertToT32(latitudeTurn0));
        int latitude1T32 = (int) Math.rint(convertToT32(latitudeTurn1));
        if (!GeoPos.isValidLatitudeT32(latitude0T32)) {
            return null;
        }
        if (!GeoPos.isValidLatitudeT32(latitude1T32)) {
            return null;
        }


        if (Double.isNaN(A0)) {
            double numberZonesLongitude0 = 1;
            return (mostRecent == 0) ?
                    new GeoPos((int) convertToT32(x0), latitude0T32) :
                    new GeoPos((int) convertToT32(x1), latitude1T32);
        }
        double numberZonesLongitude1 = zA0 - 1;
        double widthZoneLongitude0 = 1 / zA0;
        double widthZoneLongitude1 = 1 / numberZonesLongitude1;

        double zLongitude = Math.rint(x0 * numberZonesLongitude1 - x1 * zA0);
        if (mostRecent == 0) {
            double zLongitude0 = (zLongitude < 0) ? zLongitude + zA0 : zLongitude;
            double longitudeTurn = widthZoneLongitude0 * (zLongitude0 + x0);
            longitudeTurn = turnConvert(longitudeTurn);
            int longitudeT32 = (int) Math.rint(convertToT32(longitudeTurn));
            return new GeoPos(longitudeT32, latitude0T32);

        } else {
            double zLongitude1 = (zLongitude < 0) ? zLongitude + numberZonesLongitude1 : zLongitude;
            double longitudeTurn = widthZoneLongitude1 * (zLongitude1 + x1);
            longitudeTurn = turnConvert(longitudeTurn);
            int longitudeT32 = (int) Math.rint(convertToT32(longitudeTurn));
            return new GeoPos(longitudeT32, latitude1T32);
        }
    }

    private static double turnConvert(double turn) {
        return turn >= 0.5 ? turn - 1 : turn;
    }

    private static double convertToT32(double turn) {
        return Units.convert(turn, Units.Angle.TURN, Units.Angle.T32);
    }
}
