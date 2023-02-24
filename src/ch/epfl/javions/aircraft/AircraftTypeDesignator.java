package ch.epfl.javions.aircraft;
import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

public record AircraftTypeDesignator(String string) {
    static Pattern check = Pattern.compile("[A-Z0-9]{2,4}");
    public AircraftTypeDesignator{
        checkArgument(check.matcher(string).matches());
    }
}
