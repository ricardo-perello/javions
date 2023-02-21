package ch.epfl.javions;

import static ch.epfl.javions.Units.Angle.*;
import static ch.epfl.javions.Units.convert;

public record GeoPos(int longitudeT32, int latitudeT32) {

    public GeoPos{
        if (isValidLatitudeT32(latitudeT32)){
            throw new IllegalArgumentException();
        }
    }

    /**
     * Checks if latitude is valid (between -2^30 and 2^30)
     * @param latitudeT32 latitude attribute of object
     * @return boolean isValidLatitude
     */
    public static boolean isValidLatitudeT32(int latitudeT32){ return ((-(Math.scalb(1,30)) <= latitudeT32)&&
            (Math.scalb(1,30) >= latitudeT32));}

    /**
     * longitude as radian
     * @return double longitude as radian
     */
    public double longitude(){
        return convert(longitudeT32,T32,RADIAN);
    }

    /**
     * latitude as radian
     * @return double latitude as radian
     */
    public double latitude(){
        return convert(latitudeT32,T32,RADIAN);
    }

    /**
     * toString prints longitude and latitude in degrees
     * @return String longitude and latitude in degrees
     */
    @Override
    public String toString(){
        return ("("+convert(longitudeT32,T32,DEGREE)+"º, "+convert(longitudeT32,T32,DEGREE)+"º)");
    }
}

