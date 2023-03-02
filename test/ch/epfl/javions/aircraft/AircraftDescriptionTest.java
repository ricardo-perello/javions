package ch.epfl.javions.aircraft;

import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftRegistration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDescriptionTest {

    @Test
    void AircraftDescriptionThrowsIAE() {
        String string = "L2J";
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftDescription(string);
        });
    }
}
