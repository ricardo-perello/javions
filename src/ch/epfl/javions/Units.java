package ch.epfl.javions;

public class Units {
    private Units() {
    }

    public static final double CENTI = 1e-2;
    public static final double KILO = 1e3;

    /**
     * class for length
     * defining Meter, Centimeter, Kilometer, Inch, Foot & Nautical Mile
     */
    public static class Length {
        private Length() {
        }

        public static final double METER = 1;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = KILO * METER;
        public static final double INCH = 2.54 * CENTIMETER;
        public static final double FOOT = 12 * INCH;
        public static final double NAUTICAL_MILE = 1852 * METER;
    }

    /**
     * class for the angle
     * defines radian, turn, degree & T32
     */
    public static class Angle {
        private Angle() {
        }

        public static final double RADIAN = 1;
        public static final double TURN = 2 * Math.PI * RADIAN;
        public static final double DEGREE = TURN / 360;
        public static final double T32 = Math.scalb(TURN, -32);
    }

    /**
     * class for time
     * defines second, minute & hour
     */
    public static class Time {
        private Time() {
        }

        public static final double SECOND = 1;
        public static final double MINUTE = 60 * SECOND;
        public static final double HOUR = 60 * MINUTE;
    }

    /**
     * class for speed
     * defines knot, kilometer per hour
     */
    public static class Speed {
        private Speed() {
        }

        public static final double KNOT = Length.NAUTICAL_MILE / Time.HOUR;
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER / Time.HOUR;
    }

    /**
     * method that allows to convert from a unit to another
     *
     * @param value    double, is the value of the measurement we want to convert
     * @param fromUnit double, initial unit
     * @param toUnit   double, final unit
     * @return a double which is the new value converted to the new unit
     */
    public static double convert(double value, double fromUnit, double toUnit) {
        return value * (fromUnit / toUnit);
    }

    /**
     * converts from a given unit to the unit using SI
     *
     * @param value    double, value of the measurement we want to convert
     * @param fromUnit double, initial unit
     * @return a double which is the new value
     */
    public static double convertFrom(double value, double fromUnit) {
        return value * fromUnit;
    }

    /**
     * converts from a base unit to a given unit
     *
     * @param value  double, value of the measurement we want to convert
     * @param toUnit double, final unit
     * @return a double which is the new value
     */
    public static double convertTo(double value, double toUnit) {
        return value * (1 / toUnit);
    }
}
