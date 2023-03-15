package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.aircraft.IcaoAddress;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RawMessageTest {
    private final static ByteString rawMessage1 = new ByteString(new byte[]{
            (byte) 0x8D, (byte) 0x4B, (byte) 0x17, (byte) 0xE5,
            (byte) 0xF8, (byte) 0x21, (byte) 0x00, (byte) 0x02,
            (byte) 0x00, (byte) 0x4B, (byte) 0xB8, (byte) 0xB1,
            (byte) 0xF1, (byte) 0xAC});

    @Test
    void rawMessageConstructorsThrowsOnNegativeTimestamp() {
        assertThrows(IllegalArgumentException.class, () -> new RawMessage(-1, rawMessage1));
    }

    @Test
    void rawMessageConstructorsThrowsOnInvalidMessageLength() {
        assertThrows(IllegalArgumentException.class, () -> new RawMessage(8096200, new ByteString(new byte[0])));
    }

    @Test
    void typecodeWithParameterReturnsTheCorrectValue() {
        long payload = 0xF8210002004BB8L; // 1111_1000 _0010_0001 _0000_0000 _0000_0010 _0000_0000 _0100_1011 _1011_1000
        int actual = RawMessage.typeCode(payload);
        int expected = 31;
        assertEquals(expected, actual);
    }

    @Test
    void downLinkFormatReturnsTheCorrectValue() {
        RawMessage rawMessage = new RawMessage(8096200, rawMessage1);
        int actual = rawMessage.downLinkFormat();
        int expected = 17;
        assertEquals(expected, actual);
    }

    @Test
    void icaoAddressWorksOnTrivialValues() {
        RawMessage rawMessage = new RawMessage(8096200, rawMessage1);
        IcaoAddress expected = new IcaoAddress("4B17E5");
        IcaoAddress actual = rawMessage.icaoAddress();
        assertEquals(expected, actual);
    }

    @Test
    void payloadWorksOnTrivialValues() {
        RawMessage rawMessage = new RawMessage(8096200, rawMessage1);
        long expected = 0xF8210002004BB8L;
        long actual = rawMessage.payload();
        assertEquals(expected, actual);
    }

    @Test
    void typeCodeWithoutParameterWorksOnTrivialValues() {
        RawMessage rawMessage = new RawMessage(8096200, rawMessage1);
        int expected = 31;
        int actual = rawMessage.typeCode();
        assertEquals(expected, actual);
    }
}
