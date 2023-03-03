package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

public record AircraftRegistration(String string) {
    static Pattern check = Pattern.compile("[A-Z0-9 .?/_+-]+");
    public AircraftRegistration{
        checkArgument((!string.isEmpty()) && (check.matcher(string).matches()));
    }
}
