package ch.epfl.javions.demodulation;

import ch.epfl.javions.Bits;
import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class SamplesDecoder{
    private InputStream stream;
    private int batchSize;
    private Byte[] sampleDecoder;

    public SamplesDecoder(InputStream stream, int batchSize){
        Preconditions.checkArgument(batchSize > 0);
        if (batchSize == 0){
            throw new NullPointerException();
        }
        this.stream = stream;
        this.batchSize = batchSize;
        sampleDecoder = new Byte[batchSize * 2];
    }

    public int readBatch(short[] Batch) throws IOException {
        if(Batch.length != batchSize){
            throw new IllegalArgumentException();
        }
        //TODO lève IOException en cas d'erreur d'entrée/sortie
        int lengthStream = stream.available();
        byte[] nBytesRead = stream.readNBytes(batchSize);
        for (int i = 0; i < batchSize; i++) {
            byte littleEdian = (byte) (nBytesRead[i] << 8);
            byte bigEdian = (byte) (nBytesRead[i]>>>8);
            Batch[i] = (short) (littleEdian | bigEdian);
            //Batch[2 * i] =  littleEdian;
            //Batch[2 * i + 1] = bigEdian;
        }
        if (lengthStream == sampleDecoder.length) {
            return sampleDecoder.length;
        }else {
            return lengthStream / 2;
        }
        /*int byteFromStream = 0;
        byteFromStream = stream.read();
        long hexByteFull = Long.parseLong(Integer.toHexString(byteFromStream));
        sampleDecoder[2*i] = (byte) Bits.extractUInt(hexByteFull, 2, 2);*/

    }
}
