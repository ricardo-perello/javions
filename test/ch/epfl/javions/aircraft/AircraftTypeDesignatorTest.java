package ch.epfl.javions.aircraft;

import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftTypeDesignatorTest {

    @Test
    void AircraftTypeDesignatorDoesNotThrowErrorWithNull() {
        String string = null;
        assertDoesNotThrow( () -> {
            new AircraftTypeDesignator(string);
        });
    }

    @Test
    void AircraftTypeDesignatorDoesNotThrowErrorWithCorrectValue() {
        String string = "A20N";
        assertDoesNotThrow( () -> {
            new AircraftTypeDesignator(string);
        });
    }

    @Test
    void AircraftTypeDesignatorThrowsErrorWhenWrongValue() {
        String string = "A20%";
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftTypeDesignator(string);
        });
    }
}
