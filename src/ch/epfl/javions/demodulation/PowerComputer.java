package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import javax.swing.plaf.basic.BasicTableHeaderUI;
import java.io.IOException;
import java.io.InputStream;

public final class PowerComputer {
    private final short[] samplesDecoded;
    private final SamplesDecoder samplesDecoder;
    //private final ArrayList<Short> valuesA = new ArrayList<Short>(8);

    private final short[]values=new short[8];
    //private short[] values2=new short[8];
    private int evenNumbers = 0;
    private int oddNumbers = 0;


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
        Preconditions.checkArgument(batchSize > 0);
        Preconditions.checkArgument((batchSize % 8) == 0);
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


        for (int i = 0; i < batchSize; i += 8) {
            summation(0,i);
            Batch[(i / 2)] = evenNumbers * evenNumbers + oddNumbers * oddNumbers;

            substraction(2,i);
            Batch[(i / 2) +1] = evenNumbers * evenNumbers + oddNumbers * oddNumbers;

            summation(4,i);
            Batch[(i / 2)+2] = evenNumbers * evenNumbers + oddNumbers * oddNumbers;

            substraction(6,i);
            Batch[(i / 2) + 3] = evenNumbers * evenNumbers + oddNumbers * oddNumbers;
        }

        return batchSize / 2;
    }

    private void summation(int positionEven, int positionInLoop){
        evenNumbers += values[positionEven] - samplesDecoded[positionInLoop + positionEven];
        values[positionEven] = samplesDecoded[positionInLoop + positionEven];
        oddNumbers += values[positionEven +1] - samplesDecoded[positionInLoop+ positionEven + 1] ;
        values[positionEven +1] = samplesDecoded[positionInLoop + positionEven+ 1];
    }

    private void substraction(int positionEven, int positionInLoop){
        evenNumbers -= values[positionEven] - samplesDecoded[positionInLoop+positionEven];
        values[positionEven] = samplesDecoded[positionInLoop+positionEven];
        oddNumbers -= values[positionEven +1] - samplesDecoded[positionInLoop+positionEven +1];
        values[positionEven +1] = samplesDecoded[positionInLoop + positionEven +1];

    }

}
