package ch.epfl.javions.aircraft;

import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IcaoAdressTest {

    @Test
    void IcaoAdressThrowsIAE() {
        String string = "4B1814";
        assertThrows(IllegalArgumentException.class, () -> {
            new IcaoAddress(string);
        });
    }
}
