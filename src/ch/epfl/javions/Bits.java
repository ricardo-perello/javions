package ch.epfl.javions;

public class Bits {
    private Bits() {
    }

    /**
     * Extract a subset of the 64 bits from a value of type long
     *
     * @param value long
     * @param start index where the subset stars
     * @param size  size of the subset
     * @return subset of the 64 bits
     */
    public static int extractUInt(long value, int start, int size) {
        Preconditions.checkArgument(((size > 0) && (size < Integer.SIZE)));
        if (((start + size) < 0) || (start + size) > Long.SIZE) throw new IndexOutOfBoundsException();
        if (start < 0) throw new IndexOutOfBoundsException();
        long fullLong = -1;
        int newValue = (int) (value >>> start);
        long mask = fullLong >>> (64 - size);
        return (int) ((mask) & (newValue));
    }

    /**
     * testBit == 1
     *
     * @param value type long
     * @param index of bit of value
     * @return true if bit = 1
     * @throws IndexOutOfBoundsException if index not between 0-64
     */
    public static boolean testBit(long value, int index) {
        if ((index < 0) || (index > 64)) {
            throw new IndexOutOfBoundsException();
        }
        return (extractUInt(value, index, 1) == 1);
    }

}
