package ch.epfl.testcours;

import ch.epfl.javions.aircraft.AircraftRegistration;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ARTC {
    @Test
    void aircraftRegistrationConstructorThrowsWithInvalidRegistration() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftRegistration("abc");
        });
    }

    @Test
    void aircraftRegistrationConstructorThrowsWithEmptyRegistration() {
        assertThrows(IllegalArgumentException.class, () -> {
            new AircraftRegistration("");
        });
    }

    @Test
    void aircraftRegistrationConstructorAcceptsValidRegistration() {
        assertDoesNotThrow(() -> {
            new AircraftRegistration("F-HZUK");
        });
    }
}
