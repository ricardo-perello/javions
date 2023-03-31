package ch.epfl.javions.adsb;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.aircraft.IcaoAddress;
import ch.epfl.javions.demodulation.AdsbDemodulator;
import ch.epfl.javions.Units;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;

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
    @Test
    public void method2() throws IOException {
        String stream2 = getClass().getResource("/samples_20230304_1442.bin").getFile();
        stream2 = URLDecoder.decode(stream2 , StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(stream2);
        AdsbDemodulator d = new AdsbDemodulator(stream);
        RawMessage m = new RawMessage(100775400 , new ByteString(new byte[] {-115, -96, 95, 33, -101, 6, -74, -81, 24, -108, 0, -53, -61, 63}));
        System.out.println(m.typeCode());
        System.out.println(AirborneVelocityMessage.of(m));
    }

    @Test
    void ofGivesGoodVelocityGroundSpeed(){
        AirborneVelocityMessage message = AirborneVelocityMessage.of(new RawMessage(0,
                new ByteString(HexFormat.of().parseHex("8D485020994409940838175B284F"))));
        assertEquals(159.20113064925135, Units.convertTo(message.speed(),Units.Speed.KNOT));
        assertEquals(182.8803775528476, Units.convertTo(message.trackOrHeading(),Units.Angle.DEGREE));
    }

    @Test
    void ofGivesGoodVelocityAirSpeed(){
        AirborneVelocityMessage message =AirborneVelocityMessage.of(new RawMessage(0,
                new ByteString(HexFormat.of().parseHex("8DA05F219B06B6AF189400CBC33F"))));
        assertEquals(375, Units.convertTo(message.speed(),Units.Speed.KNOT));
        assertEquals(243.984375, Units.convertTo(message.trackOrHeading(),Units.Angle.DEGREE));
    }

    @Test
    void ofSubType2() {
        AirborneVelocityMessage message = AirborneVelocityMessage.of(new RawMessage(0,
                new ByteString(HexFormat.of().parseHex("8D4B1A00EA0DC89E8F7C0857D5F5"))));

        assertEquals(1061.4503686262444, message.speed());
        assertEquals(4.221861463749146, message.trackOrHeading());
    }


}
