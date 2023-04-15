package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;

public record IcaoAddress(String string) {
    final static Pattern check = Pattern.compile("[0-9A-F]{6}");
    /**
     * this method allows to verify that the string passed has the correct size and type of characters
     * @param string, String, the Aircraft's icao Adress we want to check
     */
    public IcaoAddress {
        Preconditions.checkArgument((!string.isEmpty()) && check.matcher(string).matches());
    }
}
