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
        // calculation for latitude
        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);
        double zlatitude0 = 0;
        double zlatitude1 = 0;
        double zlatitude = Math.rint(y0 * NUMBER_ZONES_LATITUDE_1 - y1 * NUMBER_ZONES_LATITUDE_0);
        if(zlatitude <0 ){
            zlatitude0 = zlatitude + NUMBER_ZONES_LATITUDE_0;
            zlatitude1 = zlatitude + NUMBER_ZONES_LATITUDE_1;
        }else{
            zlatitude0 = zlatitude;
            zlatitude1 = zlatitude;
        }
        double latitudeTurn0 = WIDTH_ZONES_LATITUDE_0 * (zlatitude0 + y0);
        double latitudeTurn1 = WIDTH_ZONES_LATITUDE_1 * (zlatitude1 + y1);
        latitudeTurn0 = turnConvert(latitudeTurn0);
        latitudeTurn1 = turnConvert(latitudeTurn1);


        if(!GeoPos.isValidLatitudeT32((int) Units.convert(latitudeTurn0,Units.Angle.TURN, Units.Angle.T32))){
            return null;
        }
        if(!GeoPos.isValidLatitudeT32((int) Units.convert( latitudeTurn1,Units.Angle.TURN, Units.Angle.T32))){
            return null;
        }

        double A = Math.acos(1 - (1 - Math.cos(2 * Math.PI * WIDTH_ZONES_LATITUDE_0)
                /Math.pow(Math.cos(Units.convert(latitudeTurn0, Units.Angle.TURN, Units.Angle.RADIAN)),2)));

        double numberOfLongitudeSections0 = Math.floor((2 * Math.PI) / A);
        if (Double.isNaN(A)){
            numberOfLongitudeSections0 = 1;
        }
        double numberOfLongitudeSections1 = numberOfLongitudeSections0 - 1;

        if(numberOfLongitudeSections0 ==1 ){
            if (mostRecent==0){
                return new GeoPos((int) x0, (int) Units.convert(latitudeTurn0, Units.Angle.TURN, Units.Angle.T32));
            }
            else{
                return  new GeoPos((int) x1, (int) Units.convert(latitudeTurn1, Units.Angle.TURN, Units.Angle.T32));
            }
        }


        double zlongitude = Math.rint(x0 * numberOfLongitudeSections1 - x1 * numberOfLongitudeSections0);
        double zlongitude0 = 0;
        double zlongitude1 = 0;
        if(zlongitude <0 ){
            zlongitude0 = zlongitude + numberOfLongitudeSections0;
            zlongitude1 = zlongitude + numberOfLongitudeSections1;
        }else{
            zlongitude0 = zlongitude;
            zlongitude1 = zlongitude;
        }

        double longitudeTurn;
        if (mostRecent == 0){
            longitudeTurn = (1/ numberOfLongitudeSections0) * (zlongitude0 + x0);
            longitudeTurn = turnConvert(longitudeTurn);
            return new GeoPos((int) Math.rint( Units.convert(longitudeTurn, Units.Angle.TURN, Units.Angle.T32)),
                    (int) Math.rint(Units.convert(latitudeTurn0, Units.Angle.TURN, Units.Angle.T32)));
        }else{
            longitudeTurn = (1/ numberOfLongitudeSections1) * (zlongitude1 + x1);
            longitudeTurn = turnConvert(longitudeTurn);
            return new GeoPos((int) Math.rint(Units.convert(longitudeTurn, Units.Angle.TURN, Units.Angle.T32)),
                    (int) Math.rint(Units.convert(latitudeTurn1, Units.Angle.TURN, Units.Angle.T32)));
        }
    }

    private static double turnConvert(double turn){
        return turn >= Units.Angle.TURN/2.0 ? turn-1 : turn;
    }
}
