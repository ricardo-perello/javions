package ch.epfl.javions.aircraft;

public enum WakeTurbulenceCategory {
    LIGHT,
    MEDIUM,
    HEAVY,
    UNKNOWN;

    /**
     * method that allows to verify that the Wake turbulence Category belong to the enumeration
     *
     * @param s, String, the Wake turbulence category of the plane in a single letter
     * @return the category from the enum above connected to the plane
     */
    public static WakeTurbulenceCategory of(String s) {
        return switch (s) {
            case "L" -> LIGHT;
            case "M" -> MEDIUM;
            case "H" -> HEAVY;
            default -> UNKNOWN;
        };
    }
}

