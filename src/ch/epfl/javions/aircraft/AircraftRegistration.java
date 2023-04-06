package ch.epfl.javions.aircraft;

import java.util.regex.Pattern;

import static ch.epfl.javions.Preconditions.checkArgument;

//todo comentarios toda esta mierda
public record AircraftRegistration(String string) {
    final static Pattern check = Pattern.compile("[A-Z0-9 .?/_+-]+");

    public AircraftRegistration {
        checkArgument((!string.isEmpty()) && (check.matcher(string).matches()));
    }
}
