package ch.epfl.javions;

import java.util.Arrays;
import java.util.HexFormat;

public final class ByteString {
    private final byte[] octetTable;

    /**
     * Creates an array of bytes that is final and unsigned
     *
     * @param bytes is byte array
     */
    public ByteString(byte[] bytes) {
        octetTable = bytes.clone();
    }

    /**
     * @param hexString string of hexadecimals
     * @return the byte string whose given string argument is the hexadecimal representation
     */
    public static ByteString ofHexadecimalString(String hexString) {
        Preconditions.checkArgument(((hexString.length()) % 2) == 0);
        HexFormat hf = HexFormat.of().withUpperCase();
        return new ByteString(hf.parseHex(hexString));
    }

    /**
     * size of octetTable
     *
     * @return int length of octetTable
     */
    public int size() {
        return octetTable.length;
    }

    public int byteAt(int index) {
        if (index >= octetTable.length || index < 0) {
            throw new IndexOutOfBoundsException();
        }
        return Byte.toUnsignedInt(octetTable[index]);
    }

    /**
     * concatenation of bytes in a given range
     *
     * @param fromIndex start byte
     * @param toIndex   end byte
     * @return long type concatenation of bytes from start byte to end byte.
     */
    public long bytesInRange(int fromIndex, int toIndex) {
        Preconditions.checkArgument((toIndex - fromIndex) < 8);
        if ((toIndex > size()) || (fromIndex < 0)) throw new IndexOutOfBoundsException();
        long output = 0;
        for (int i = fromIndex; i < toIndex; i++) {
            output = ((output << Byte.SIZE) | byteAt(i));
        }
        return output;
    }


    /**
     * method that compares two byteStrings
     *
     * @param comparedObject Object, element compared to
     * @return a boolean, true if same, false if different
     */
    public boolean equals(Object comparedObject) {
        return comparedObject instanceof ByteString byteStringCompared
                && Arrays.equals(byteStringCompared.octetTable, octetTable);
    }

    /**
     * method for hashcode of octetTable
     *
     * @return int, hashCode of octetTable
     */
    public int hashCode() {
        return Arrays.hashCode(octetTable);
    }

    /**
     * method turns octetTable into String
     *
     * @return string related to octetTable
     */
    public String toString() {
        HexFormat hf = HexFormat.of().withUpperCase();
        return hf.formatHex(octetTable);
    }

}
