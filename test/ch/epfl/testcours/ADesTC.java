package ch.epfl.testcours;

import ch.epfl.javions.aircraft.AircraftDescription;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ADesTC {
    @Test
    void aircraftDescriptionConstructorThrowsWithInvalidDescription() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftDescription("abc");
        });
    }

    @Test
    void aircraftDescriptionConstructorAcceptsEmptyDescription() {
        assertDoesNotThrow(() -> {
            new AircraftDescription("");
        });
    }

    @Test
    void aircraftDescriptionConstructorAcceptsValidDescription() {
        assertDoesNotThrow(() -> {
            new AircraftDescription("A0E");
        });
    }
}
