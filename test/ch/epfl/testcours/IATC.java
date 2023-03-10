package ch.epfl.testcours;

import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IATC {
    @Test
    void icaoAddressConstructorThrowsWithInvalidAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            new IcaoAddress("00000a");
        });
    }

    @Test
    void icaoAddressConstructorThrowsWithEmptyAddress() {
        assertThrows(IllegalArgumentException.class, () -> {
            new IcaoAddress("");
        });
    }

    @Test
    void icaoAddressConstructorAcceptsValidAddress() {
        assertDoesNotThrow(() -> {
            new IcaoAddress("ABCDEF");
        });
    }
}
