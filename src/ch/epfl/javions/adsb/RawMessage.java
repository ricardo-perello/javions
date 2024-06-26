package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

import java.util.HexFormat;

public record RawMessage(long timeStampNs, ByteString bytes) {
    public static final int LENGTH = 14;
    private static final int END_PAYLOAD = 11;
    private static final Crc24 crc24 = new Crc24(Crc24.GENERATOR);
    private static final int VALUE_DF = 17;
    private static final int START_TYPECODE = 51;
    private static final int LENGTH_TYPECODE = 5;

    private static final int START_PAYLOAD = 4;


    /**
     * constructor of RawMessage
     *
     * @param timeStampNs, long, time of the message in nanoseconds
     * @param bytes,       ByteString, bytes of message
     */
    public RawMessage {
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(bytes.size() == LENGTH);
    }

    /**
     * method that verifies if the Crc24 works
     *
     * @param timerStampNS long, time of the message in nanoseconds
     * @param bytes        byte[], byte of message
     * @return iff the result of crc24 is 0 then a new RawMessage with parameter timerStampsNS
     * and a byteString derived from bytes; if not, null
     */
    public static RawMessage of(long timerStampNS, byte[] bytes) {
        return crc24.crc(bytes) == 0 ? new RawMessage(timerStampNS, new ByteString(bytes)) : null;
    }

    /**
     * method size verifies the DL
     *
     * @param byte0 byte, first byte of the message you want to demodularize
     * @return int, if DL equals 17, then LENGTH <=> 14, if not, 0
     */
    public static int size(byte byte0) {
        return Byte.toUnsignedInt(byte0) >> 3 == VALUE_DF ? LENGTH : 0;
    }

    /**
     * method that allows to find the code type of the message passed as argument
     *
     * @param payload long, message we want to find the ME
     * @return int, the first 5 bits of the ME
     */
    public static int typeCode(long payload) {
        return Bits.extractUInt(payload, START_TYPECODE, LENGTH_TYPECODE);
    }

    /**
     * method that find the DF
     *
     * @return the DF found in the first 3 bits of the first byte
     */
    public int downLinkFormat() {
        return (byte) (bytes.byteAt(0) >> 3);
    }

    /**
     * method that find the ICAO of the message
     *
     * @return IcaoAddress, the icao address of the message found from the 1st to 3rd (included) bytes
     */
    public IcaoAddress icaoAddress() {
        return new IcaoAddress(HexFormat.of().withUpperCase().toHexDigits(bytes.bytesInRange(1, 4), 6));
    }

    /**
     * method that finds the important part of the message ( the ME)
     *
     * @return long, the ME found from the 4rth to thr 11th(excluded) bytes of the message
     */
    public long payload() {
        return bytes.bytesInRange(START_PAYLOAD, END_PAYLOAD);
    }

    public int typeCode() {
        return typeCode(payload());
    }
}
