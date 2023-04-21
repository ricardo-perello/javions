package ch.epfl.javions.gui;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;
import org.junit.jupiter.api.Test;

import javax.imageio.IIOException;
import java.io.*;

public class ObservavbleAircraftStateTest {
    @Test
    void test1() throws IOException {
        try (DataInputStream s = new DataInputStream(
                new BufferedInputStream(
                        new FileInputStream("resources/messages_20230318_0915.bin")))){
            byte[] bytes = new byte[RawMessage.LENGTH];
            while (true) {
                long timeStampNs = s.readLong();
                int bytesRead = s.readNBytes(bytes, 0, bytes.length);
                assert bytesRead == RawMessage.LENGTH;
                ByteString message = new ByteString(bytes);
                System.out.printf("%13d: %s\n", timeStampNs, message);
            }
        } catch (EOFException e) {}

    }

}
