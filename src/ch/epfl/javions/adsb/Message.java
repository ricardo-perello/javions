package ch.epfl.javions.adsb;

import ch.epfl.javions.aircraft.IcaoAddress;

public interface Message {
    //TODO comentarios
    long timeStampNs();
    IcaoAddress icaoAddress();
}
