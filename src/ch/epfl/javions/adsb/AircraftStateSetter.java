package ch.epfl.javions.adsb;

import ch.epfl.javions.GeoPos;

public interface AircraftStateSetter {
    //TODO comentarios
    void setLastMessageTimeStampNs(long timeStampsNs);

    void setCategory(int category);

    void setCallSign(CallSign callSign);

    void setPosition(GeoPos position);

    void setAltitude(double altitude);

    void setVelocity(double velocity);

    void setTrackOrHeading(double trackOrHeading);
}
