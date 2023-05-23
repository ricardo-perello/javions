package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

public record AircraftDescription(String string) {
    final static Pattern check = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    /**
     * this method allows to verify that the string passed has the correct size and type of characters
     *
     * @param string, String, the Aircraft description we want to check
     */
    public AircraftDescription {
        checkArgument(string.isEmpty() || (check.matcher(string).matches()));
    }
}
