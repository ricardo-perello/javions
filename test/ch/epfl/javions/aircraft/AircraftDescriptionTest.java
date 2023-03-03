package ch.epfl.javions.aircraft;

import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftRegistration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftDescriptionTest {

    @Test
    void AircraftDescriptionDoesNotThrowErrorWithNull() {
        String string = "";
        assertDoesNotThrow( () -> {
            new AircraftDescription(string);
        });
    }

    @Test
    void AircraftDescriptionDoesNotThrowErrorWithCorrectValue() {
        String string = "L2J";
        assertDoesNotThrow( () -> {
            new AircraftDescription(string);
        });
    }

    @Test
    void AircraftDescriptionThrowsErrorWhenWrongValue() {
        String string = "L2W";
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftDescription(string);
        });
    }
}
