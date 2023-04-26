package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

public record AircraftRegistration(String string) {
    final static Pattern check = Pattern.compile("[A-Z0-9 .?/_+-]+");
    /**
     * this method allows to verify that the string passed has the correct size and type of characters
     * @param string, String, the Aircraft registration we want to check
     */
    public AircraftRegistration {
        checkArgument((!string.isEmpty()) && (check.matcher(string).matches()));
    }
}
