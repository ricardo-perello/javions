package ch.epfl.javions.adsb;

import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class AircraftIdentificationMessageTest {

    @Test
    void TestRawMessage() throws IOException {
        String f = "resources/samples_20230304_1442.bin";
        int idx=0;
        try (InputStream s = new FileInputStream(f)) {
            AdsbDemodulator d = new AdsbDemodulator(s);
            RawMessage m;
            while ((m = d.nextMessage()) != null){
                System.out.println(AircraftIdentificationMessage.of(m));
                idx++;
            }

        }
        System.out.println(idx);
    }
}
