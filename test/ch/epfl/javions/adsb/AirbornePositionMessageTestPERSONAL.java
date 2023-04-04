package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class AirbornePositionMessageTestPERSONAL {

    @Test
    public void testWithQEqualToZero() {
        ByteString bs = ByteString.ofHexadecimalString("8D39203559B225F07550ADBE328F");
        ByteString bs2 = ByteString.ofHexadecimalString("8DAE02C85864A5F5DD4975A1A3F5");
        RawMessage mess1 = new RawMessage(0, bs);
        RawMessage mess2 = new RawMessage(0, bs2);

        AirbornePositionMessage m1 = AirbornePositionMessage.of(mess1);
        AirbornePositionMessage m2 = AirbornePositionMessage.of(mess2);

        assertEquals(3474.72,m1.altitude(), 0.01);
        assertEquals( 7315.20, m2.altitude(), 0.01);
    }

    @Test
    public void throwsAllErrors(){
        assertThrows(NullPointerException.class, () -> new AirbornePositionMessage(0,null,89,
                1,0.1,0.3));
        assertThrows(IllegalArgumentException.class, ()-> new AirbornePositionMessage(-1,
                new IcaoAddress("4D2228"), 89, 0, 0.2,0.2));
        assertThrows(IllegalArgumentException.class, ()-> new AirbornePositionMessage(8989,
                new IcaoAddress("4D2228"), 89, 5, 0.2,0.2));
        assertThrows(IllegalArgumentException.class, ()-> new AirbornePositionMessage(1111,
                new IcaoAddress("4D2228"), 89, 0, 1,0.2));
        assertThrows(IllegalArgumentException.class, ()-> new AirbornePositionMessage(4354,
                new IcaoAddress("4D2228"), 89, 0, 567,0.2));
        assertThrows(IllegalArgumentException.class, ()-> new AirbornePositionMessage(1234567,
                new IcaoAddress("4D2228"), 89, 0, 0.2,1));
        assertThrows(IllegalArgumentException.class, ()-> new AirbornePositionMessage(1234,
                new IcaoAddress("4D2228"), 89, 0, 0.2,8989));
        assertDoesNotThrow(()-> new AirbornePositionMessage(0,
                new IcaoAddress("000000"), 788, 1, 0.0, 0.0));
    }
    @Test
    void TestRawMessage() throws IOException {
        String f = "resources/samples_20230304_1442.bin";
        int idx=0;
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            while ((m = d.nextMessage()) != null){
                System.out.println(AirbornePositionMessage.of(m));
                idx++;
            }

        }
        System.out.println(idx);
    }

    @Test
    void airbornePositionMessageReturnsNull(){
        ByteString m0 = ByteString.ofHexadecimalString("8D3920351F54245F0E550ADBE28F");
        ByteString m5 = ByteString.ofHexadecimalString("8D3920351FBA245F0E550ADBE28F");
        ByteString m6 = ByteString.ofHexadecimalString("8D3920351F9A245F0E550ADBE28F");

        RawMessage mess0 = new RawMessage(0, m0);
        RawMessage mess5 = new RawMessage(0, m5);
        RawMessage mess6 = new RawMessage(0, m6);

        assertNull(AirbornePositionMessage.of(mess0));
        assertNull(AirbornePositionMessage.of(mess5));
        assertNull(AirbornePositionMessage.of(mess6));
    }
}
