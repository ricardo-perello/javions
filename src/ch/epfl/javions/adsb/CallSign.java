package ch.epfl.javions.adsb;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;
public record CallSign(String string) {
    final static Pattern check = Pattern.compile("[A-Z0-9 ]{0,8}");
    /**
     * this method allows to verify that the string passed has the correct size and typer of characters
     * @param string, String, the Aircraft's Call sign we want to check
     */
    public CallSign {
        checkArgument((string.isEmpty()) || (check.matcher(string).matches()));
    }

}
