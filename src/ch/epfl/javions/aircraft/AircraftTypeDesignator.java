package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

public record AircraftTypeDesignator(String string) {
    final static Pattern check = Pattern.compile("[A-Z0-9]{2,4}");
    /**
     * this method allows to verify that the string passed has the correct size and type of characters
     * @param string, String, the Aircraft type designator we want to check
     */
    public AircraftTypeDesignator {
        checkArgument(string.isEmpty() || (check.matcher(string).matches()));
    }
}
