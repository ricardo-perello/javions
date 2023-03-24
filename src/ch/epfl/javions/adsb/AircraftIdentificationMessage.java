package ch.epfl.javions.adsb;

import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.Objects;

import static ch.epfl.javions.Bits.extractUInt;
import static ch.epfl.javions.adsb.RawMessage.typeCode;

public record AircraftIdentificationMessage(long timeStampNs,IcaoAddress icaoAddress, int category,
                                            CallSign callSign) implements Message {
    private final static String ALPHABET = "?ABCDEFGHIJKLMNOPQRSTUVWXYZ????? ???????????????0123456789??????????????????????????????????????????????????????????";
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
        for (int i = 0; i < 8; i++) {
            string += ALPHABET.charAt(extractUInt(payload, i*6, 6));

        }
        string = string.stripTrailing();
        if (string.contains("?")) return null;

        return new AircraftIdentificationMessage(rawMessage.timeStampNs(), rawMessage.icaoAddress(),
                cat, new CallSign(string));
    }

    @Override
    public long timeStampNs() {
        return timeStampNs;
    }

    @Override
    public IcaoAddress icaoAddress() {
        return icaoAddress;
    }

}
