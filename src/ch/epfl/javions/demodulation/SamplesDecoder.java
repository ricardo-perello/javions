package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public final class SamplesDecoder {
    private final InputStream stream;
    private final int batchSize;
    private final byte[] littleEndian;
    private final int RECENTER = 2048;

    /**
     * constructor of SampleDecoder
     *
     * @param stream    the InputStream from where we are going to get the values
     * @param batchSize the size of the batch we are going to study
     */
    public SamplesDecoder(InputStream stream, int batchSize) {
        Objects.requireNonNull(stream);
        Preconditions.checkArgument(batchSize > 0);
        this.stream = stream;
        this.batchSize = batchSize;
        littleEndian = new byte[batchSize * 2];
    }

    /**
     * method that allows to decode the values that we have sampled
     *
     * @param Batch table where we put in the value decoded for every i
     * @return int, number of batches decoded
     * or length of the stream devided by two (and rounded downwards) if there are not enough in the stream to fill the batchSize
     * @throws IOException exception in case of error in the input / output
     */
    public int readBatch(short[] Batch) throws IOException {
        Preconditions.checkArgument(Batch.length == batchSize);
        int nBytesRead = stream.readNBytes(littleEndian, 0, 2 * batchSize) / 2;
        for (int i = 0; i < nBytesRead; i++) {
            Batch[i] = (short) ((((littleEndian[i * 2 + 1] & 0xFF) << 8) |
                    (littleEndian[i * 2] & 0xFF)) - RECENTER);
        }
        return nBytesRead;
    }
}
