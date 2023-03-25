package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder {

    private static final double NORMALIZER = Math.pow(2, -17);
    private static final double NUMBER_ZONES_LATITUDE_0 = 60.0;
    private static final double NUMBER_ZONES_LATITUDE_1 = 59.0;
    private static final double WIDTH_ZONES_LATITUDE_0 = 1.0/NUMBER_ZONES_LATITUDE_0;
    private static final double WIDTH_ZONES_LATITUDE_1 = 1.0/NUMBER_ZONES_LATITUDE_1;



    /**
     * constructor for CprDecoder
     * @param x0 double, local longitude of an even message
     * @param y0 double, local latitude of an even message
     * @param x1 double, local longitude of an odd message
     * @param y1 double, local latitude of an odd message
     * @param mostRecent int tells us the parity of the message
     * @return GeoPos, the position of the plane
     */
    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        // TODO null si position ne peut pas Être déterminé en raison de changement de bande de latitude

        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);

        double zLatitude = Math.rint(y0 * NUMBER_ZONES_LATITUDE_1 - y1 * NUMBER_ZONES_LATITUDE_0);
        double zLatitude0 = (zLatitude<0) ? zLatitude + NUMBER_ZONES_LATITUDE_0 : zLatitude;
        double zLatitude1 = (zLatitude<0) ? zLatitude + NUMBER_ZONES_LATITUDE_1 : zLatitude;
        double latitudeTurn = (mostRecent == 0) ? WIDTH_ZONES_LATITUDE_0 * (zLatitude0 + y0) : WIDTH_ZONES_LATITUDE_1 * (zLatitude1 + y1);
        latitudeTurn = turnConvert(latitudeTurn);
        int latitudeT34 = (int) Units.convert(latitudeTurn, Units.Angle.TURN, Units.Angle.T32);
        if(!GeoPos.isValidLatitudeT32(latitudeT34)){
            return null;
        }

        double A = Math.acos(1-(1-Math.cos(2*Math.PI*WIDTH_ZONES_LATITUDE_0))/Math.pow(Math.cos(Units.convert(latitudeTurn, Units.Angle.TURN,Units.Angle.RADIAN)),2));
        double numberZonesLongitude0 = Double.isNaN(A) ? 1 : Math.floor((2 * Math.PI)/A);
        double numberZonesLongitude1 = numberZonesLongitude0 -1;
        double widthZoneLongitude0 = 1/numberZonesLongitude0;
        double widthZoneLongitude1 = 1/numberZonesLongitude1;


        if(numberZonesLongitude0 == 1){
            return new GeoPos((int) x0, latitudeT34);
        }

        double zLongitude = Math.rint(x0 * numberZonesLongitude1 - x1 * numberZonesLongitude0);
        double zLongitude0 = (zLongitude<0) ? zLongitude + numberZonesLongitude0 : zLongitude;
        double zLongitude1 = (zLongitude<0) ? zLongitude + numberZonesLongitude1 : zLongitude;
        double longitudeTurn = (mostRecent == 0) ? widthZoneLongitude0 * (zLongitude0 + x0) : widthZoneLongitude1 * (zLongitude1 + x1);
        longitudeTurn = turnConvert(longitudeTurn);
        int longitudeT34 = (int) Units.convert(longitudeTurn, Units.Angle.TURN, Units.Angle.T32);

        return new GeoPos(longitudeT34, latitudeT34);
    }

    private static double turnConvert(double turn){
        return turn >= 0.5 ? turn-1 : turn;
    }
}
