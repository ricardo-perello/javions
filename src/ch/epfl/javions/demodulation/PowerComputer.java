package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer {
    private int[] batch;
    private short[] decodedSampleTable;
    private int batchSize;
    private SamplesDecoder samplesDecoder;

    /**
     * constructor of PowerComputer,
     *
     * @param stream    the InputStream from where we are going to get the values from
     * @param batchSize the size of the batch we are going to study
     */
    public PowerComputer(InputStream stream, int batchSize) {
        this.batchSize = batchSize;
        //we verify that batchSize is possible and a multiple of 8
        Preconditions.checkArgument(this.batchSize > 0);
        Preconditions.checkArgument((this.batchSize % 8) == 0);
        batch = new int[this.batchSize];
        decodedSampleTable = new short[this.batchSize];
        samplesDecoder = new SamplesDecoder(stream, this.batchSize);
    }

    /**
     * method that does the calculation to give the power of the values we have sampled
     *
     * @param Batch table where we put in the value the power for every i
     * @return int, the number of batches of power that have been placed in the Batch[]
     * @throws IOException exception in case of error in the input / output
     */

    public int readBatch(int[] Batch) throws IOException {
        Preconditions.checkArgument(Batch.length == (batchSize / 2));
        samplesDecoder.readBatch(decodedSampleTable);
        short[] values = new short[8];
        int counter = 0;
        for (int i = 0; i < batchSize; i += 2) {
            //For every i in the loop, we add two new values into the table values from the table decodedSampleTable found
            //using samplesDecoder.readBatch .
            //They are placed at their position using the modulo of 8, this allows us to take out the values of the
            //previous batch at the same position.
            //His allows us to always have the eight values that interest us in the same table.
            //It does not matter if they are not in order since a sum commutative.
            values[i % 8] = decodedSampleTable[i];
            values[(i + 1) % 8] = decodedSampleTable[i + 1];
            //We then select the values that interest us using the ints evenNumbers & oddNumbers
            int evenNumbers = (int) Math.pow((values[(i + 2) % 8]) - (values[(i + 4) % 8])
                    + (values[(i + 6) % 8]) - (values[(i) % 8]), 2);
            int oddNumbers = (int) Math.pow((values[(i + 1) % 8]) - (values[(i + 3) % 8])
                    + (values[(i + 5) % 8]) - (values[(i + 7) % 8]), 2);
            Batch[i / 2] = evenNumbers + oddNumbers;
            counter++;
        }
        return counter;
    }


}
