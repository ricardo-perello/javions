package ch.epfl.javions;

import java.util.HexFormat;

public final class ByteString{
    byte[] octetTable;
    /**
     * Creates an array of bytes that is final and unsigned
     * @param bytes is byte array
     */
    public ByteString(byte[] bytes){ //todo check if final
        byte[] octetTableCopy = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++){
            octetTableCopy[i] = (byte) Byte.toUnsignedInt(bytes[i]);
            octetTable = octetTableCopy.clone();
        }
    }

    /**
     *
     * @param hexString string of hexadecimals
     * @return the byte string whose given string argument is the hexadecimal representation
     */
    public static ByteString ofHexadecimalString(String hexString){
        Preconditions.checkArgument(((hexString.length())%2) == 0);
        // TODO: 23/02/2023 NumberFormatException
        HexFormat hf = HexFormat.of().withUpperCase();
        return new ByteString(hf.parseHex(hexString));
    }

    public int size(){
        return octetTable.length;
    }

    public int byteAt(int index){
        if (index > size()){
            throw new IndexOutOfBoundsException();
        }
        return octetTable[index];
    }

    /**
     * concatenation of bytes in a given range
     * @param fromIndex start byte
     * @param toIndex end byte
     * @return long type concatenation of bytes from start byte to end byte.
     */
    public long bytesInRange(int fromIndex, int toIndex){
        long output = 0;
        int counter = 0;
        for (int i = fromIndex; i < toIndex; i++){
            output = output + ((long) octetTable[i] << (counter*8));
            counter++;
        }
        return output;
    }
}
