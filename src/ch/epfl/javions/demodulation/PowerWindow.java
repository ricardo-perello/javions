package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {
    final static int BATCH_SIZE = (int) Math.pow(2, 16);
    private InputStream stream;
    private int windowSize;
    private int windowPositionInsideBatch;
    private int absoluteWindowPosition;
    private PowerComputer powerComputer;
    private int[] array1 = new int[BATCH_SIZE];
    private int[] array2 = new int[BATCH_SIZE];
    private int numOfSamples;
    private int SamplesLeft;


    /**
     * constructor for PowerWindow
     *
     * @param stream     the InputStream from where we are going to get the values
     * @param windowSize the size of the window we are going to study
     * @throws IOException exception thrown if error in intput/output
     */
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument((windowSize > 0) && (windowSize <= BATCH_SIZE));
        this.stream = stream;
        this.windowSize = windowSize;
        powerComputer = new PowerComputer(this.stream, BATCH_SIZE);
        numOfSamples = powerComputer.readBatch(array1);
        SamplesLeft = numOfSamples;
    }

    /**
     * method for the size of the window
     *
     * @return int, window size
     */
    public int size() {
        return windowSize;
    }

    /**
     * method for the position
     *
     * @return int, position of the window in the stream
     */
    public long position(){return absoluteWindowPosition;}

    /**
     * method to determine if window is full
     *
     * @return boolean, true while windowPosition and windowSize is smaller than the numOfSample
     */
    public boolean isFull() {return (windowSize <= SamplesLeft);}

    /**
     * method get, that allows to find the value of the power at the index i
     * @param i, int, position we are interested in
     * @return the value of the power at index i
     */
    public int get(int i){
        if (!((i >= 0)&&(i < windowSize))){
            throw new IndexOutOfBoundsException();
        }
        if (windowPositionInsideBatch + i < BATCH_SIZE){
            return array1[windowPositionInsideBatch +i];
        }
        else{
            return array2[i - (BATCH_SIZE- windowPositionInsideBatch)];
        }
    }

    /**
     * method that allows us to advance the window by 1
     * @throws IOException error of the input / output from the powerComputer.readBatch
     */
    public void advance() throws IOException{
        windowPositionInsideBatch++;
        absoluteWindowPosition++;
        SamplesLeft--;
        if (windowPositionInsideBatch + windowSize -1 == BATCH_SIZE) {
            SamplesLeft += powerComputer.readBatch(array2);
        }
        if (windowPositionInsideBatch == BATCH_SIZE){
            switchArray();
            windowPositionInsideBatch = 0;
        }
    }

    /**
     * method that allows to advance by offsetposition
     * @param offset int, the number of position we want to move by
     * @throws IOException exception related to advance()
     */
    public void advanceBy(int offset) throws IOException{
        Preconditions.checkArgument(offset >= 0);
        for (int i = 0; i < offset; i++) { 
            advance();
        }
    }

    private void switchArray(){
        int[] tempArray = array2;
        array2 = array1;
        array1 = tempArray;
    }

}
