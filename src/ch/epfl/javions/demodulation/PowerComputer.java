package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public final class PowerComputer {
    private final short[] samplesDecoded;
    private final SamplesDecoder samplesDecoder;
    private final ArrayList<Short> valuesA = new ArrayList<Short>(8);
    private final short[]values = new short[8];
    private final short[]values1=new short[8];
    private short[] values2=new short[8];



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
        //TODO encontrar pq da error
        /*for (int i = 0; i < 8; i++) {
            valuesA.add((short) 0);
        }
        for (int i = 0; i < batchSize; i+=2) {
            valuesA.add(0, samplesDecoded[i]);
            valuesA.add(1,samplesDecoded[i+1]);
            valuesA.remove(9);
            valuesA.remove(8);
            int evenNumbers = valuesA.get(2) - valuesA.get(4) + valuesA.get(6) - valuesA.get(0);
            int oddNumbers = valuesA.get(3) - valuesA.get(5) + valuesA.get(7) - valuesA.get(1);
            Batch[(i/2)] = evenNumbers * evenNumbers + oddNumbers * oddNumbers;

            /*for (int j = 0; j < 7; j+=2) {
                // TODO prefuntar pq no puedo usar ArrayList.removeRange(...)
                //valuesA.remove(j);
                valuesA.add(0, samplesDecoded[i+j]);
                //valuesA.remove(j+1);
                valuesA.add(1, samplesDecoded[i+j+1]);
                valuesA.remove(9);
                valuesA.remove(8);
                int evenNumbers = valuesA.get(2) - valuesA.get(4) + valuesA.get(6) - valuesA.get(0);
                int oddNumbers = valuesA.get(3) - valuesA.get(5) + valuesA.get(7) - valuesA.get(1);
                Batch[(i + j/2)] = evenNumbers * evenNumbers + oddNumbers * oddNumbers;
            }
        }*/

        /*for (int i = 0; i < batchSize; i+=2) {
            values1[0] = samplesDecoded[i];
            values1[1]= samplesDecoded[i+1];
            int evenNumbers = values1[2] - values1[4] + values1[6] - values1[0];
            int oddNumbers = values1[3] - values1[5] + values1[7] - values1[1];
            Batch[(i/2)] = evenNumbers * evenNumbers + oddNumbers * oddNumbers;
            values2 = values1.clone();
            for (int j = 0; j < 6; j++) {
                values1[j+2] = values2[j];
            }
        }*/
        /*for (int i = 0; i < batchSize; i+=2) {
            valuesA.add(0,samplesDecoded[i]);
            valuesA.add(1,samplesDecoded[i+1]);
            values1= valuesA.toArray(new Short[8]);
            int evenNumbers = valuesA.get(2) - values1[4] + values1[6] - values1[0];
            int oddNumbers = values1[3] - values1[5] + values1[7] - values1[1];
        }*/



        for (int i = 1; i < batchSize; i += 2) {
            //For every i in the loop, we add two new values into the table values from the table decodedSampleTable found
            //using samplesDecoder.readBatch .
            //They are placed at their position using the modulo of 8, this allows us to take out the values of the
            //previous batch at the same position.
            //His allows us to always have the eight values that interest us in the same table.
            //It does not matter if they are not in order since a sum commutative.
            //TODO quitar modulo mirar audio enviado a ricardo

            values[i % 8] = samplesDecoded[i];
            values[(i - 1) % 8] = samplesDecoded[i - 1];
            //We then select the values that interest us using the ints evenNumbers & oddNumbers
            int evenNums = values[2]- values[4] + values[6] - values[0];
            int oddNums = values[1] - values[3] + values[5] - values[7];
            Batch[(i - 1) / 2] = evenNums * evenNums + oddNums * oddNums;
        }
        return batchSize / 2;
    }
}
