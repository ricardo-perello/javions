package ch.epfl.javions;

public final class Bits {
    private final static long FULL_LONG = -1;
    private static final int SIZE_LONG = 64;

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
        int newValue = (int) (value >>> start);
        long mask = FULL_LONG >>> (SIZE_LONG - size);
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
        if ((index < 0) || (index > SIZE_LONG)) {
            throw new IndexOutOfBoundsException();
        }
        return (extractUInt(value, index, 1) == 1);
    }

}
