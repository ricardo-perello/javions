package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

public record AircraftDescription(String string) {
    final static Pattern check = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");

    public AircraftDescription {
        checkArgument(string.isEmpty() || (check.matcher(string).matches()));
    }
}
