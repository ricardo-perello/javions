package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

import static ch.epfl.javions.Bits.extractUInt;
import static ch.epfl.javions.adsb.RawMessage.typeCode;

public record AircraftIdentificationMessage(long timeStampNs, IcaoAddress icaoAddress, int category,
                                            CallSign callSign) implements Message {
    @SuppressWarnings("SpellCheckingInspection")
    private final static String ALPHABET = "?ABCDEFGHIJKLMNOPQRSTUVWXYZ????? ???????????????0123456789??????";
    private static final int CAT_START = 48;
    /**
     * Register that takes in a RawMessage and returns timeStamp, icao address, category and call-sign.
     * Compact constructor checks if the timestamp is bigger than 0. Also checks that ICAO address and
     * callsign are not null.
     *
     * @param timeStampNs timestamp
     * @param icaoAddress ICAO address
     * @param category    category
     * @param callSign    callSign
     */
    public AircraftIdentificationMessage {
        Preconditions.checkArgument(timeStampNs >= 0);
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
    }

    /**
     * Creates an instance of an AircraftIdentificationMessage using the RawMessage as an input.
     *
     * @param rawMessage raw message which contains all the information needed to create the message.
     * @return AircraftIdentificationMessage of RawMessage.
     */
    public static AircraftIdentificationMessage of(RawMessage rawMessage) {
        long payload = rawMessage.payload();
        int typeCode = typeCode(payload);
        if ((typeCode < 0) || (typeCode > 4)) {
            return null;
        }
        int cat = (((14 - typeCode) << 4) | (extractUInt(payload, CAT_START, 3)));
        StringBuilder string = new StringBuilder();
        for (int i = 7; i >= 0; i--) {
            string.append(ALPHABET.charAt(extractUInt(payload, i * 6, 6)));
        }
        string = new StringBuilder(string.toString().stripTrailing());
        if (string.toString().contains("?")) {
            return null;
        }

        return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(),
                cat, new CallSign(string.toString()));
    }

    /**
     * timestamp
     *
     * @return timestamp
     */
    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    /**
     * ICAO address
     *
     * @return icao Address
     */
    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }

}
