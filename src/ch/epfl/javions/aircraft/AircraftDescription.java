package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

public record AircraftDescription(String string) {
    static Pattern check = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");
    public AircraftDescription{
        checkArgument(check.matcher(string).matches());
    }
}
