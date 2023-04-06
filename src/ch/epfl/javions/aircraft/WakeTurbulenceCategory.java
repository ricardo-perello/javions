package ch.epfl.javions.aircraft;

public enum WakeTurbulenceCategory {
    LIGHT,
    MEDIUM,
    HEAVY,
    UNKNOWN;
    // TODO: 6/4/23 comments
    public static WakeTurbulenceCategory of(String s) {
        return switch (s) {
            case "L" -> LIGHT;
            case "M" -> MEDIUM;
            case "H" -> HEAVY;
            default -> UNKNOWN;
        };
    }
}

