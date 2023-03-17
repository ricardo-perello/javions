package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Units;

public class CprDecoder {

    private static final double NORMALIZER = Math.pow(2, -17);


    public static GeoPos decodePosition(double x0, double y0, double x1, double y1, int mostRecent) {
        double latitude0 = y0 * NORMALIZER;
        double latitude1 = y1 * NORMALIZER;
        double zlatitude = Math.rint(latitude0 * 59 - latitude1 * 60);
        double latitudeTurn;
        if (mostRecent == 0) {
            latitudeTurn = 1 / 60 * (zlatitude + latitude0);
        } else {
            latitudeTurn = 1 / 59 * (zlatitude + latitude1);
        }

        double A = Math.acos(1 - (1 - Math.cos(2 * Math.PI * 1 / 60)) /
                Math.pow(2, Math.cos(Units.convert(latitudeTurn, Units.Angle.TURN, Units.Angle.DEGREE))));

        double numberOfLongitudeSections0 = Math.floor(2 * Math.PI / A);
        double numberOfLongitudeSections1 = numberOfLongitudeSections0 - 1;

        double longitude0 = x0 * NORMALIZER;
        double longitude1 = x1 * NORMALIZER;

        double zlongitude = Math.rint(longitude0 * numberOfLongitudeSections1 - longitude1 * numberOfLongitudeSections0);

        double longitudeTurn;
        if (mostRecent == 0){
            longitudeTurn = 1/ numberOfLongitudeSections0 * (zlongitude + longitude0);
        }else{
            longitudeTurn = 1/ numberOfLongitudeSections1 * (zlongitude + longitude1);
        }
        return new GeoPos((int) Units.convert(longitudeTurn, Units.Angle.TURN, Units.Angle.T32),
                (int) Units.convert(latitudeTurn, Units.Angle.TURN, Units.Angle.T32));
    }

}
