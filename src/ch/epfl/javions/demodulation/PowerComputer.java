package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer {
    private final short[] samplesDecoded;
    private final SamplesDecoder samplesDecoder;

    private final short[] values = new short[8];
    private int evenNumbers = 0;
    private int oddNumbers = 0;

    private final int LENGTH_BYTE = 8;

    /**
     * constructor of PowerComputer,
     * we verify that batchSize is possible and a multiple of 8
     * we initialise the private attributes batch, samplesDecoded, samplesDecoder
     * batch => int table that we will use to store the values of readBatch
     * samplesDecoded => short table where we store all the value of SamplesDecoded
     * samplesDecoder => SamplesDecoder attribute that allows us to use SamplesDecoder.readBatch(...)
     *
     * @param stream    the InputStream from where we are going to get the values from
     * @param batchSize the size of the batch we are going to study
     */

    public PowerComputer(InputStream stream, int batchSize) {
        Preconditions.checkArgument(batchSize > 0 &&
                (batchSize % LENGTH_BYTE) == 0);
        samplesDecoded = new short[batchSize * 2];
        samplesDecoder = new SamplesDecoder(stream, 2 * batchSize);

    }

    /**
     * method that does the calculation to give the power of the values we have sampled
     *
     * @param Batch table where we put in the value the power for every i
     * @return int, the number of batches of power that have been placed in the Batch[]
     * @throws IOException exception in case of error in the input / output
     */
    public int readBatch(int[] Batch) throws IOException {
        Preconditions.checkArgument(Batch.length == (samplesDecoded.length / 2));
        int batchSize = samplesDecoder.readBatch(samplesDecoded);


        for (int i = 0; i < batchSize; i += LENGTH_BYTE) {
            evenNumbers += newNumbers(0, i);
            oddNumbers += newNumbers(1, i);
            Batch[(i / 2)] = evenNumbers * evenNumbers + oddNumbers * oddNumbers;

            evenNumbers -= newNumbers(2, i);
            oddNumbers -= newNumbers(3, i);
            Batch[(i / 2) + 1] = evenNumbers * evenNumbers + oddNumbers * oddNumbers;

            evenNumbers += newNumbers(4, i);
            oddNumbers += newNumbers(5, i);
            Batch[(i / 2) + 2] = evenNumbers * evenNumbers + oddNumbers * oddNumbers;

            evenNumbers -= newNumbers(6, i);
            oddNumbers -= newNumbers(7, i);
            Batch[(i / 2) + 3] = evenNumbers * evenNumbers + oddNumbers * oddNumbers;
        }
        return batchSize / 2;
    }

    /**
     * private method that allows to determine the value we add or subtract from either evenNumbers or oddNumbers
     *
     * @param positionNumber  int, position in the table values to find the previous number to eliminate it
     * @param positionInLoop, position in loop that allows to find the value at the position in the table samplesDecoded
     * @return the new number we need to add to the sum we are interested in
     */
    private int newNumbers(int positionNumber, int positionInLoop) {
        int nb = values[positionNumber] - samplesDecoded[positionInLoop + positionNumber];
        values[positionNumber] = samplesDecoded[positionInLoop + positionNumber];
        return nb;
    }
}
