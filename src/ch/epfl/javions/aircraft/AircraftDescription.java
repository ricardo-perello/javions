package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

public class AircraftDescription {
    static Pattern check = Pattern.compile("[ABDGHLPRSTV-][0123468][EJPT-]");
    public AircraftDescription(String string) {
        checkArgument(check.matcher(string).matches());
    }
}
