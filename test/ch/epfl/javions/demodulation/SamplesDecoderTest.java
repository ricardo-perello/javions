package ch.epfl.javions.demodulation;

import ch.epfl.javions.demodulation.SamplesDecoder;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SamplesDecoderTest {
    @Test
    public void SamplesDecodeThrowsExceptions() throws FileNotFoundException {
        String samples = getClass().getResource("/samples.bin").getFile();
        samples = URLDecoder.decode(samples, UTF_8);
        InputStream stream = new FileInputStream(samples);
        assertThrows(NullPointerException.class, () -> new SamplesDecoder(null, 3));
        assertThrows(IllegalArgumentException.class, () -> new SamplesDecoder(stream, -3));
    }


    @Test
    void testValidSampleDecoder() throws IOException {
        String stream2 = getClass().getResource("/samples.bin").getFile();
        stream2 = URLDecoder.decode(stream2, UTF_8);
        InputStream stream = new FileInputStream(stream2);
        SamplesDecoder test = new SamplesDecoder(stream, 2402);
        System.out.println(8 << 8);
        short[] batch = new short[2402];
        test.readBatch(batch);
        assertEquals(-3, batch[0]);
        assertEquals(8, batch[1]);
        assertEquals(-9, batch[2]);
        assertEquals(-8, batch[3]);
        assertEquals(-5, batch[4]);
        assertEquals(-8, batch[5]);
        assertEquals(-12, batch[6]);
        assertEquals(-16, batch[7]);
        assertEquals(-23, batch[8]);
        assertEquals(-9, batch[9]);

    }

    @Test
    void readBatchTest() throws IOException {
        short[] expected = new short[]{-3, 8, -9, -8, -5, -8, -12, -16, -23, -9};
        short[] actual = new short[1200];

        InputStream stream = new FileInputStream("resources/samples.bin");
        SamplesDecoder a = new SamplesDecoder(stream, 1200);
        int b = a.readBatch(actual);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

    @Test
    void testValidSampleDecoder1() throws IOException {
        String stream2 = getClass().getResource("/samples.bin").getFile();
        stream2 = URLDecoder.decode(stream2, UTF_8);
        InputStream stream = new FileInputStream(stream2);
        SamplesDecoder test = new SamplesDecoder(stream, 2402);

        short[] batch = new short[2402];
        test.readBatch(batch);
        for (int i = 0; i < 10; ++i) {
            System.out.println(batch[i]);
        }

    }

    @Test
    void readBatchWorks() throws IOException {
        InputStream stream = new FileInputStream("resources/samples.bin");
        try {
            short[] tab = new short[4804];
            SamplesDecoder decode = new SamplesDecoder(stream, 4804);
            decode.readBatch(tab);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void readBatchTest1 () throws IOException {
        short[] expected = new short[]{-3, 8, -9, -8, -5, -8, -12, -16, -23, -9};
        short[] actual = new short[1200];

        InputStream stream = new FileInputStream("resources/samples.bin");
        SamplesDecoder a = new SamplesDecoder(stream, 1200);
        int b = a.readBatch(actual);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }
}
