package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;


public class MessageParser {

    public static Message parse(RawMessage rawMessage) {
        long rawMessageMe = rawMessage.payload();
        int typeOfMessage = Bits.extractUInt(rawMessageMe, 48, 3);
        if (typeOfMessage >= 1 && typeOfMessage <= 4) {
            return AircraftIdentificationMessage.of(rawMessage);
        }
        if ((typeOfMessage >= 9 && typeOfMessage <= 18) || (typeOfMessage >= 20 && typeOfMessage <= 22)) {
            return AirbornePositionMessage.of(rawMessage);
        }
        //TODO poner la clase que falta
        /*if(typeOfMessage == 19){
            return ...
        }*/
        return null;
    }
}
