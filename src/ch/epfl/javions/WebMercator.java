package ch.epfl.javions;

import static ch.epfl.javions.Math2.asinh;
import static ch.epfl.javions.Units.Angle.RADIAN;
import static ch.epfl.javions.Units.Angle.TURN;
import static ch.epfl.javions.Units.convert;

public class WebMercator {
    private WebMercator(){}

    /**
     * Projection x
     * @param zoomLevel zoom level from 0-19
     * @param longitude longitude in radians
     * @return x-coordinate of the map given the zoomlevel and the longitude
     */
    public static double x(int zoomLevel, double longitude){
        return ((Math.scalb(1,8+zoomLevel))*(convert(longitude,RADIAN,TURN)+0.5));
    }

    /**
     * Projection y
     * @param zoomLevel zoom level from 0-19
     * @param latitude latitude in radians
     * @return y-coordinate of the map given the zoomlevel and the latitude
     */
    public static double y(int zoomLevel, double latitude){
        return ((Math.scalb(1,8+zoomLevel))*((-(convert(asinh(Math.tan(latitude)),RADIAN,TURN)))+0.5));
    }


}
