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
        int nBytesRead = stream.readNBytes(littleEndian, 0,2 * batchSize);
        for (int i = 0; i < batchSize; i++) {
            bigEndian[2*i] = littleEndian[2*i+1];
            bigEndian[2*i+1] = littleEndian[2*i];
            System.out.println(bigEndian[2*i]<<8);
            Batch[i] = (short) (((bigEndian[2*i]<<8)|bigEndian[2*i+1])-2048);
        }
        /*
        for (int i = 0; i < batchSize; i++) {
            byte littleEndian = (byte) (sampleDecoder[2 * i] << 8);
            byte bigEndian = (byte) (sampleDecoder[2 * i + 1] >>> 8);
            Batch[i] = (short) (littleEndian | bigEndian);
        }

         */
        if (lengthStream == littleEndian.length) {
            return batchSize / 2;
        } else {
            return (int) Math.floor(lengthStream / 2);
        }
    }
}
