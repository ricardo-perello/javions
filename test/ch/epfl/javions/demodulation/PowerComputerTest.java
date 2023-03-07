package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PowerComputerTest {

    @Test
    void testValidSampleDecoder() throws IOException {
        String stream2 = getClass().getResource("/samples.bin").getFile();
        stream2 = URLDecoder.decode(stream2, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(stream2);
        PowerComputer test = new PowerComputer(stream, 32);
        //System.out.println(8 << 8);
        int[] batch = new int[32];
        test.readBatch(batch);
        assertEquals(73, batch[0]);
        assertEquals(292, batch[1]);
        assertEquals(65, batch[2]);
        assertEquals(745, batch[3]);
        assertEquals(98, batch[4]);
        assertEquals(4226, batch[5]);
        assertEquals(12244, batch[6]);
        assertEquals(25722, batch[7]);
        assertEquals(36818, batch[8]);
        assertEquals(23825, batch[9]);

    }




}
