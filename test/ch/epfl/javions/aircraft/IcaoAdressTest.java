package ch.epfl.javions.aircraft;

import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IcaoAdressTest {

    @Test
    void IcaoAdressDoesNotThrowErrorWithNull() {
        String string = null;
        assertThrows(IllegalArgumentException.class, () -> {
            new IcaoAddress(string);
        });
    }

    @Test
    void IcaoAdressDoesNotThrowErrorWithCorrectValue() {
        String string = "4B1814";
        assertDoesNotThrow( () -> {
            new IcaoAddress(string);
        });
    }

    @Test
    void IcaoAdressThrowsErrorWhenWrongValue() {
        String string = "4B1814D";
        assertThrows(IllegalArgumentException.class, () -> {
            new IcaoAddress(string);
        });
    }
}
