package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

import static ch.epfl.javions.Bits.extractUInt;
import static ch.epfl.javions.adsb.RawMessage.typeCode;

public record AircraftIdentificationMessage(long timeStampNs,IcaoAddress icaoAddress, int category,
                                            CallSign callSign) implements Message {
    private final static String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ????? ???????????????0123456789";
    public AircraftIdentificationMessage{
        Preconditions.checkArgument(timeStampNs >= 0);
        Objects.requireNonNull(icaoAddress);
        Objects.requireNonNull(callSign);
    }

    public static AircraftIdentificationMessage of(RawMessage rawMessage){
        long payload = rawMessage.payload();
        int typeCode = typeCode(payload);
        int cat = (((14-typeCode)<<4)|(extractUInt(payload, 48, 3)));
        String string = "";
        string += (ALPHABET.charAt(extractUInt(payload, 42, 6)))  +  (ALPHABET.charAt(extractUInt(payload, 36, 6)))+
                (ALPHABET.charAt(extractUInt(payload, 30, 6))) + (ALPHABET.charAt(extractUInt(payload, 24, 6))) +
                (ALPHABET.charAt(extractUInt(payload, 18, 6))) + (ALPHABET.charAt(extractUInt(payload, 12, 6))) +
                (ALPHABET.charAt(extractUInt(payload, 6, 6))) + (ALPHABET.charAt(extractUInt(payload, 0, 6)));

        return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(),
                cat, new CallSign(string));
    }

    @Override
    public long timeStampNs() {
        return 0;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return null;
    }

}
