package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.Units;

public class CprDecoder {
    private static final double HALF_TURN = 0.5;
    private static final double NUMBER_ZONES_LATITUDE_0 = 60.0;
    private static final double NUMBER_ZONES_LATITUDE_1 = 59.0;
    private static final double WIDTH_ZONES_LATITUDE_0 = widthCalculator(NUMBER_ZONES_LATITUDE_0);
    private static final double WIDTH_ZONES_LATITUDE_1 = widthCalculator(NUMBER_ZONES_LATITUDE_1);


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
        // latitude
        Preconditions.checkArgument(mostRecent == 0 || mostRecent == 1);
        double zLatitude = Math.rint(y0 * NUMBER_ZONES_LATITUDE_1 - y1 * NUMBER_ZONES_LATITUDE_0);
        double zLatitude0 = zCoordinatesPositive(zLatitude, NUMBER_ZONES_LATITUDE_0);
        double zLatitude1 = zCoordinatesPositive(zLatitude, NUMBER_ZONES_LATITUDE_1);
        double latitudeTurn0 = coordinateTurnCalculator(WIDTH_ZONES_LATITUDE_0, zLatitude0, y0);
        double latitudeTurn1 = coordinateTurnCalculator(WIDTH_ZONES_LATITUDE_1, zLatitude1, y1);

        //longitude
        double numberZonesLongitude0 = numberZonesLongitudeCalculator(latitudeTurn0, latitudeTurn1);
        if (Double.isNaN(numberZonesLongitude0)) {
            return null;
        }
        double numberZonesLongitude1 = numberZonesLongitude0 - 1;
        double widthZoneLongitude0 = widthCalculator(numberZonesLongitude0);
        double widthZoneLongitude1 = widthCalculator(numberZonesLongitude1);
        double zLongitude = Math.rint(x0 * numberZonesLongitude1 - x1 * numberZonesLongitude0);


        if (mostRecent == 0) {
            return geoPosComputer(latitudeTurn0, zLongitude, numberZonesLongitude0, numberZonesLongitude0,
                    widthZoneLongitude0, x0);
        } else {
            return geoPosComputer(latitudeTurn1, zLongitude, numberZonesLongitude0, numberZonesLongitude1,
                    widthZoneLongitude1, x1);
        }
    }

    /**
     * private method that allows to verify that turn is not over 0.5 turn
     *
     * @param turn, double the angle in turn that we want to verify
     * @return double, if turn >= 0.5, we return turn - 1, if not, we return turn
     * this means that the value returned belongs to [-0.5 , 0.5[
     */
    private static double turnVerifier(double turn) {
        return turn >= HALF_TURN ? turn - 1 : turn;
    }


    /**
     * private method that allows us to calculate A
     *
     * @param latitudeTurn, double angle og the latitude of the plane, in turn, used in the formula
     * @return double, we return A (formula below)
     */
    private static double ACalculator(double latitudeTurn) {
        return Math.acos(1 - (1 - Math.cos(2 * Math.PI * WIDTH_ZONES_LATITUDE_0)) /
                Math.pow(Math.cos(Units.convert(latitudeTurn, Units.Angle.TURN, Units.Angle.RADIAN)), 2));
    }

    /**
     * private method that allows to calculate zA (number of zone for the longitude)
     *
     * @param A, double, valued calculated in the previous method
     * @return int, return the number of zone for the longitude
     */
    private static int zACalculator(double A) {
        return (int) Math.floor((2 * Math.PI) / A);
    }

    /**
     * private method that allows us to calculate the angle of the plane in turns
     *
     * @param widthZone,         double, width of the zone the plane is currently in
     * @param numberZone,        double, number of zones (differs depending on the MostRecent and longitude/latitude)
     * @param initialCoordinate, double initial coordinates of the plane (differs depending on the MostRecent and longitude/latitude)
     * @return the angle of the plane coordinate in turn
     */
    private static double coordinateTurnCalculator(double widthZone, double numberZone, double initialCoordinate) {
        return widthZone * (numberZone + initialCoordinate);
    }

    /**
     * private method that allows to make sure that number of zones depending on the direction and most recent is positive
     *
     * @param zCoordinates           double, the number of zones we want to make sure about
     * @param numberZonesCoordinates double the number of zones general for the direction
     * @return double, the definitive angle
     */
    private static double zCoordinatesPositive(double zCoordinates, double numberZonesCoordinates) {
        return (zCoordinates < 0) ? zCoordinates + numberZonesCoordinates : zCoordinates;
    }

    /**
     * method that allows us to determine the width of the zone thanks to the number of zones
     *
     * @param numberOfZones, double, number of zones in a given direction(longitude or latitude)
     * @return double the width
     */
    private static double widthCalculator(double numberOfZones) {
        return 1.0 / numberOfZones;
    }

    /**
     * private method that allows to calculate the final position of the plane
     *
     * @param latitudeTurn,                  double, the latitude in its turn form
     * @param zLongitude,                    double, current longitude zone
     * @param numberZonesLongitude0,         double, the number of zones for the longitude for MostRecent = 0
     * @param numberZonesLongitudeMostRecent double, the number of zones for the longitude depending on the mostRecent
     * @param widthZoneLongitude,            double, the width of zones for the longitude depending on the mostRecent
     * @param x,                             double, the coordinates for x depending on the mostRecent
     * @return null if the Latitude is not valid
     * new GeoPos of the plane (formula changes depending on the conditions)
     */
    private static GeoPos geoPosComputer(double latitudeTurn, double zLongitude, double numberZonesLongitude0,
                                         double numberZonesLongitudeMostRecent, double widthZoneLongitude, double x) {

        latitudeTurn = turnVerifier(latitudeTurn);
        int latitudeT32 = (int) Math.rint(Units.convert(latitudeTurn, Units.Angle.TURN, Units.Angle.T32));

        if (!GeoPos.isValidLatitudeT32(latitudeT32)) {
            return null;
        }
        if (numberZonesLongitude0 == 1) {
            return new GeoPos((int) Units.convert(x, Units.Angle.TURN, Units.Angle.T32), latitudeT32);
        }

        double zLongitudeMostResent = zCoordinatesPositive(zLongitude, numberZonesLongitudeMostRecent);
        double longitudeTurn = coordinateTurnCalculator(widthZoneLongitude, zLongitudeMostResent, x);
        longitudeTurn = turnVerifier(longitudeTurn);
        int longitudeT32 = (int) Math.rint(Units.convert(longitudeTurn, Units.Angle.TURN, Units.Angle.T32));
        return new GeoPos(longitudeT32, latitudeT32);
    }


    /**
     * private method that allows to calculate the number of zones for the longitude
     *
     * @param latitudeTurn0, double the latitude in turns with Most Recent as 0
     * @param latitudeTurn1, double the latitude in turns with Most Recent as 1
     * @return NaN is zA1 and zA0 not equal, 1 if A0 is NaN or zA0
     */
    private static double numberZonesLongitudeCalculator(double latitudeTurn0, double latitudeTurn1) {
        double A0 = ACalculator(latitudeTurn0);
        double A1 = ACalculator(latitudeTurn1);
        double zA0 = zACalculator(A0);
        double zA1 = zACalculator(A1);

        if (zA1 != zA0 && !Double.isNaN(A0) && !Double.isNaN(A1)) {
            return Double.NaN;
        }

        return Double.isNaN(A0) ? 1 : zA0;

    }

}
