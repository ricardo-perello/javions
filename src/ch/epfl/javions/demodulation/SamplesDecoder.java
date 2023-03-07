package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class SamplesDecoder {
    private InputStream stream;
    private int batchSize;
    private byte[] littleEndian;
    private int[] bigEndian;
    private int RECENTER = 2048;

    public SamplesDecoder(InputStream stream, int batchSize) {
        Objects.requireNonNull(stream);
        Preconditions.checkArgument(batchSize > 0);
        this.stream = stream;
        this.batchSize = batchSize;
        littleEndian = new byte[batchSize * 2];
        bigEndian = new int[batchSize * 2];
    }

    public int readBatch(short[] Batch) throws IOException {
        Preconditions.checkArgument(Batch.length == batchSize);
        int lengthStream = stream.available();
        int nBytesRead = stream.readNBytes(littleEndian, 0, 2 * batchSize);
        for (int i = 0; i < nBytesRead / 2; i++) {
            Batch[i] = (short) ((((littleEndian[i * 2 + 1] & 0xFF) << 8) | (littleEndian[i * 2] & 0xFF)) - RECENTER);
        }
        if (lengthStream == littleEndian.length) {
            return batchSize / 2;
        } else {
            return (int) Math.floor(lengthStream / 2);
        }
    }
}
