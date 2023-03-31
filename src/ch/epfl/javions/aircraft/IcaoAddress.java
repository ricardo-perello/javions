package ch.epfl.javions.aircraft;

import ch.epfl.javions.Preconditions;

import java.util.regex.Pattern;


public record IcaoAddress(String string) {
    static Pattern check = Pattern.compile("[0-9A-F]{6}");

    public IcaoAddress {
        Preconditions.checkArgument((!string.isEmpty()) && check.matcher(string).matches());
    }
}
