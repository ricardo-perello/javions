package ch.epfl.javions.adsb;

import ch.epfl.javions.Bits;
import ch.epfl.javions.ByteString;
import ch.epfl.javions.Crc24;
import ch.epfl.javions.Preconditions;
import ch.epfl.javions.aircraft.IcaoAddress;

public record RawMessage(long timeStampNs, ByteString bytes) {
    //TODO comentarios
    public static final int LENGTH = 14;
    private static final Crc24 crc24 = new Crc24(Crc24.GENERATOR);

    /**
     * constructor of RawMessage
     * @param timeStampNs, long, time of the message in nanoseconds
     * @param bytes, ByteString, bytes of message
     */
    public RawMessage{
        Preconditions.checkArgument(timeStampNs >= 0);
        Preconditions.checkArgument(bytes.size() == LENGTH);
    }

    /**
     * method that verifies if the Crc24 works
     * @param timerStampNS long, time of the message in nanoseconds
     * @param bytes byte[], byte of message
     * @return iff the result of crc24 is 0 then a new RawMessage with parameter timerStampsNS
     * and a byteString derived from bytes; if not, null
     */
    public static RawMessage of(long timerStampNS, byte[] bytes){
        return crc24.crc(bytes) == 0 ? new RawMessage(timerStampNS, new ByteString(bytes)) : null;
    }

    public static int size(byte byte0){
        return  Byte.toUnsignedInt(byte0)>>3 ==  17 ? LENGTH : 0;
    }

    public static int typeCode(long payload){
        return Bits.extractUInt(payload, 51,5);
    }

    public int downLinkFormat(){
        return (byte)(bytes.byteAt(0)>>3);
    }

    public IcaoAddress icaoAddress(){
        return new IcaoAddress(Long.toString(bytes.bytesInRange(1,4)).toUpperCase());
    }

    public long payload(){
        return bytes.bytesInRange(4,11);
    }

    public int typeCode(){
        return typeCode(payload());
    }
}