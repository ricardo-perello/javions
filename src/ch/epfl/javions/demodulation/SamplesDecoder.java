package ch.epfl.javions.demodulation;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class SamplesDecoder {
    private InputStream stream;
    private int batchSize;
    private byte[] sampleDecoder;

    public SamplesDecoder(InputStream stream, int batchSize) {
        Objects.requireNonNull(stream);
        Preconditions.checkArgument(batchSize > 0);
        this.stream = stream;
        this.batchSize = batchSize;
        sampleDecoder = new byte[batchSize * 2];
    }

    public int readBatch(short[] Batch) throws IOException {
        Preconditions.checkArgument(Batch.length == batchSize);
        int lengthStream = stream.available();
        int nBytesRead = stream.readNBytes(sampleDecoder, 0, batchSize);
        for (int i = 0; i < batchSize; i++) {
            byte littleEdian = (byte) (sampleDecoder[2 * i] << 8);
            byte bigEdian = (byte) (sampleDecoder[2 * i + 1] >>> 8);
            Batch[i] = (short) (littleEdian | bigEdian);
        }
        if (lengthStream == sampleDecoder.length) {
            return batchSize / 2;
        } else {
            return (int) Math.floor(lengthStream / 2);
        }
    }
}
