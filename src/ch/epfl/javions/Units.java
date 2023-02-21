package ch.epfl.javions;

public class Units {
    private Units(){}
//Alejandro ES MUY PUTA
    public static final double CENTI = 1e-2;
    public static final double KILO = 1e3;

    public static class Length {
        private Length(){}
        public static final double METER = 1;
        public static final double CENTIMETER = CENTI * METER;
        public static final double KILOMETER = KILO * METER;
        public static final double INCH = 2.54 * CENTIMETER;
        public static final double FOOT = 12 * INCH;
        public static final double NAUTICAL_MILE = 1852 * METER;
    }

    public static class Angle{
        private Angle(){}
        public static final double RADIAN = 1;
        public static final double TURN = 2 * Math.PI * RADIAN;
        public static final double DEGREE = TURN / 360;
        public static final double T32 = TURN / 2e32;
    }

    public static class Time{
        private Time(){}
        public static final double SECOND = 1;
        public static final double MINUTE = 60 * SECOND;
        public static final double HOUR = 60 * MINUTE;
    }

    public static class Speed {
        private Speed(){}
        public static final double KNOT = Length.NAUTICAL_MILE / Time.HOUR;
        public static final double KILOMETER_PER_HOUR = Length.KILOMETER / Time.HOUR;
    }
}
