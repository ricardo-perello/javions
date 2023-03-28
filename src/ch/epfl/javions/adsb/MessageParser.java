package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;


public class MessageParser {

    public static Message parse(RawMessage rawMessage) {
        long rawMessageMe = rawMessage.payload();
        int typeOfMessage = Bits.extractUInt(rawMessageMe, 48, 3);
        return switch (typeOfMessage) {
            case 1, 2, 3, 4 -> AircraftIdentificationMessage.of(rawMessage);
            case 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22 -> AirbornePositionMessage.of(rawMessage);
            case 19 -> AirborneVelocityMessage.of(rawMessage);
            default -> null;
        };
    }
}
