package ch.epfl.javions;

public class Bits {
    private Bits(){}

    /**
     * Extract a subset of the 64 bits from a value of type long
     * @param value long
     * @param start index where the subset stars
     * @param size size of the subset
     * @return subset of the 64 bits
     */
    public static int extractUInt(long value, int start, int size){
        if (!((size > 0)&&(size < Integer.SIZE))) throw new IllegalArgumentException();
        if (((start+size)<0)||(start+size)>=Long.SIZE) throw new IndexOutOfBoundsException(); //todo check with mateus
        long fullLong = -1;
        long newValue = value >> start;
        long mask = fullLong >> (64-size);
        return (int) ((mask)&(newValue));
    }
    public static boolean testBit(long value, int index){
        return true;
    }

}
