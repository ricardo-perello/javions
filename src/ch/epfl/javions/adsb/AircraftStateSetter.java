package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public interface AircraftStateSetter {

    /**
     * abstract method that allows change the timestamp of the last message
     * received from the aircraft to the given value,
     *
     * @param timeStampsNs long, value we will change to
     */
    void setLastMessageTimeStampNs(long timeStampsNs);

    /**
     * abstract method that allows change the category of the aircraft to the given value
     *
     * @param category, int, value we will change to
     */
    void setCategory(int category);

    /**
     * abstract method that allows to change the aircraft callsign to the given value
     *
     * @param callSign, CallSign, value we will change to
     */
    void setCallSign(CallSign callSign);

    /**
     * abstract method that allows to change the position of the aircraft to the given value
     *
     * @param position, GeoPos, value we will change to
     */
    void setPosition(GeoPos position);

    /**
     * abstract method that allows to change the altitude of the aircraft to the given value,
     *
     * @param altitude double, value we will change to
     */
    void setAltitude(double altitude);

    /**
     * abstract method that allows to change the speed of the aircraft to the given value
     *
     * @param velocity, double, value we will change to
     */
    void setVelocity(double velocity);

    /**
     * abstract method that allows to change the direction of the aircraft to the given value
     *
     * @param trackOrHeading, double, value we will change to
     */
    void setTrackOrHeading(double trackOrHeading);
}
