package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

public record AircraftRegistration(String string) {
    static Pattern check = Pattern.compile("[A-Z0-9 .?/_+-]+");
    public AircraftRegistration{
        checkArgument(check.matcher(string).matches());
    }
}
