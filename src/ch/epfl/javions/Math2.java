package ch.epfl.javions;

public class Math2 {
    private Math2() {
    }

    /**
     * Clamp: limits value v to interval min-max
     *
     * @param min lower bound
     * @param v   value
     * @param max higher bound
     * @return v if v lies between min and max.
     * if v lower than min, return min.
     * if v greater than max, return max.
     */
    public static int clamp(int min, int v, int max) {
        Preconditions.checkArgument(min <= max);
        if (v < min) return min;
        else return Math.min(v, max);
    }

    /**
     * Arcsinh of x
     *
     * @param x angle
     * @return arcsinh of x
     */
    public static double asinh(double x) {
        return ((Math.log(x + (Math.sqrt(1 + (x * x))))));
    }
}
