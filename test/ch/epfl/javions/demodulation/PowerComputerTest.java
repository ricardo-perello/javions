package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class PowerComputerTest {

    @Test
    void testValidSampleDecoder() throws IOException {
        String stream2 = getClass().getResource("/samples.bin").getFile();
        stream2 = URLDecoder.decode(stream2, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(stream2);
        PowerComputer test = new PowerComputer(stream, 2400);
        //System.out.println(8 << 8);
        int[] batch = new int[2400];
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

    @Test
    void readBatchTest() throws IOException {
        int[] expected = new int[]{73, 292, 65, 745, 98, 4226, 12244, 25722, 36818, 23825};
        int[] actual = new int[600];

        InputStream stream = new FileInputStream("resources/samples.bin");
        PowerComputer a = new PowerComputer(stream,1200);
        int b = a.readBatch(actual);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected[i],actual[i]);
        }
    }

    @Test
    void constructorThrowsOnInvalidBatchSize() {
        assertThrows(IllegalArgumentException.class, () -> new
                PowerComputer(new ByteArrayInputStream(new byte[0]), -1));
        assertThrows(IllegalArgumentException.class, () -> new
                PowerComputer(new ByteArrayInputStream(new byte[0]), 7));
    }

    @Test
    void constructorDoesNotThrowOnValidArguments() {
        assertDoesNotThrow(() -> new PowerComputer(new
                ByteArrayInputStream(new byte[0]), 8));
    }

    @Test
    void readBatchThrowsOnInvalidBatchsize() throws IOException {
        InputStream stream = new FileInputStream("resources/samples.bin");
        PowerComputer powerComputer = new PowerComputer(stream, 64);
        int[] actual = new int[128]; // 64 * 2, could be any number != 64
        assertThrows(IllegalArgumentException.class, () ->
                powerComputer.readBatch(actual));
    }

    @Test
    void readBatchTest2() throws IOException {
        int[] expected = new int[]{73, 292, 65, 745, 98, 4226, 12244, 25722,
                36818, 23825};
        int[] actual = new int[1200];
        InputStream stream = new FileInputStream("resources/samples.bin");
        PowerComputer powerComputer = new PowerComputer(stream, 1200);
        int powered = powerComputer.readBatch(actual);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected[i], actual[i]);
        }
        assertEquals(actual.length, powered);
    }
}
