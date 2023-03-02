package ch.epfl.javions.aircraft;

import ch.epfl.javions.aircraft.AircraftDescription;
import ch.epfl.javions.aircraft.AircraftTypeDesignator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftTypeDesignatorTest {

    @Test
    void AircraftTypeDesignatorThrowsIAE() {
        String string = "A20N";
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftTypeDesignator(string);
        });
    }
}
