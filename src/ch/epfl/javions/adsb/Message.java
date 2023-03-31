package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

public interface Message {

    /**
     * Abstract method timestamp
     *
     * @return the timestamp of the message, in nanoseconds
     */
    long timeStampNs();

    /**
     * Abstract method icaoAddress
     *
     * @return the ICAO address of the sender of the message.
     */
    IcaoAddress icaoAddress();
}
