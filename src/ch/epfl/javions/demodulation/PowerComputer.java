package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer {
    private int[] batch;
    private short[] samplesDecoded;
    private int batchSize;
    private SamplesDecoder samplesDecoder;

    public PowerComputer(InputStream stream, int batchSize){
        this.batchSize = batchSize;
        Preconditions.checkArgument(batchSize > 0);
        Preconditions.checkArgument((batchSize % 8) == 0);
        batch = new int[batchSize];
        samplesDecoded = new short[batchSize];
        samplesDecoder = new SamplesDecoder(stream, batchSize);
    }

    public int readBatch(int[] Batch) throws IOException {
        Preconditions.checkArgument(Batch.length == batchSize);
        samplesDecoder.readBatch(samplesDecoded);
        short[] values = new short[8];
        int counter = 0;
        for (int i = 0; i < batchSize; i+=2) {
            values[i%8] = samplesDecoded[i];
            values[(i+1)%8] = samplesDecoded[i+1];
            int evenNums = (int) Math.pow((values[(i+2)%8])-(values[(i+4)%8])+(values[(i+6)%8])-(values[(i)%8]),2);
            int oddNums = (int) Math.pow((values[(i+1)%8])-(values[(i+3)%8])+(values[(i+5)%8])-(values[(i+7)%8]),2);
            Batch[i / 2] = evenNums + oddNums;
            counter++;

        }
        return counter;
    }


}
