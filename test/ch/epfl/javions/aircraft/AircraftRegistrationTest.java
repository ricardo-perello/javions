package ch.epfl.javions.aircraft;

import ch.epfl.javions.aircraft.AircraftRegistration;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AircraftRegistrationTest {

    @Test
    void AircraftRegistrationDoesNotThrowErrorWithNull() {
        String string = null;
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftRegistration(string);
        });
    }

    @Test
    void AircraftRegistrationDoesNotThrowErrorWithCorrectValue() {
        String string = "HB-JDC";
        assertDoesNotThrow( () -> {
            new AircraftRegistration(string);
        });
    }

    @Test
    void AircraftRegistrationThrowsErrorWhenWrongValue() {
        String string = "HB-JDC&";
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftRegistration(string);
        });
    }

}
