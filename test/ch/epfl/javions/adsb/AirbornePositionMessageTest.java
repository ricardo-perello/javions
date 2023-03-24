package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AirbornePositionMessageTest {

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
}
