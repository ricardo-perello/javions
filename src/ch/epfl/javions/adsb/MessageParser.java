package ch.epfl.javions.adsb;

public class MessageParser {
    /**
     * method allowing to determine what type of message the parameter is
     *
     * @param rawMessage RawMessage, message we want to determine what type of message it is
     * @return AircraftIdentificationMessage.of, if the parameter's type code is between 1 and 4 included,
     * AirbornePositionMessage.of, if the parameter's type code is between 9 and 18 or 20 and 22,
     * AirborneVelocityMessage.of, if equal to 19,
     * if the parameter's type code does not corresponds to one of these conditions, we return null
     */
    public static Message parse(RawMessage rawMessage) {
        int typeOfMessage = rawMessage.typeCode();
        return switch (typeOfMessage) {
            case 1, 2, 3, 4 -> AircraftIdentificationMessage.of(rawMessage);
            case 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 20, 21, 22 -> AirbornePositionMessage.of(rawMessage);
            case 19 -> AirborneVelocityMessage.of(rawMessage);
            default -> null;
        };
    }

}
