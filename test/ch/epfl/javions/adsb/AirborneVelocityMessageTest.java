package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class AirborneVelocityMessageTest {

    @Test
    public void method() throws IOException {
        String stream2 = getClass().getResource("/samples_20230304_1442.bin").getFile();
        stream2 = URLDecoder.decode(stream2, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(stream2);
        AdsbDemodulator d = new AdsbDemodulator(stream);
        RawMessage m;
        while ((m = d.nextMessage()) != null) {
            if (m.typeCode() == 19 && AirborneVelocityMessage.of(m) != null) {
                System.out.println(AirborneVelocityMessage.of(m));
            }
        }
    }
}
