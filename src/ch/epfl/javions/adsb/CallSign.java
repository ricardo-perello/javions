package ch.epfl.javions.adsb;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;
    // todo comentario
public record CallSign(String string) {
    static Pattern check = Pattern.compile("[A-Z0-9 ]{0,8}");

    public CallSign {
        checkArgument((string.isEmpty()) || (check.matcher(string).matches()));
    }

}
