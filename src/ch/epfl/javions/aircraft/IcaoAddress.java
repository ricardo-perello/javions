package ch.epfl.javions.aircraft;
import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

public record IcaoAddress(String string) {
    static Pattern check = Pattern.compile("[0-9A-F]{6}");
    public IcaoAddress{
        checkArgument(check.matcher(string).matches());
    }
}
