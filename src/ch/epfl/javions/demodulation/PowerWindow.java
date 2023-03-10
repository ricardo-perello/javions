package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {
    final static int BATCH_SIZE = (int) Math.pow(2, 16);
    private InputStream stream;
    private int windowSize;
    private int windowPosition;
    private int absoluteWindowPosition;
    private PowerComputer powerComputer;
    private int[] array1 = new int[BATCH_SIZE];
    private int[] array2 = new int[BATCH_SIZE];
    private int numOfSamples;
    private int SamplesLeft;

//todo add comments

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
        powerComputer = new PowerComputer(stream, BATCH_SIZE);
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
    public boolean isFull() {return (windowPosition+windowSize < SamplesLeft);}

    public int get(int i){
        if (!((i >= 0)&&(i < windowSize))){
            throw new IndexOutOfBoundsException();
        }
        if (windowPosition + i < numOfSamples){
            return array1[windowPosition+i];
        }
        else{
            int newIndex =(i - (numOfSamples-windowPosition));
            return array2[newIndex];
        }
    }

    public void advance() throws IOException{
        windowPosition++;
        absoluteWindowPosition++;
        SamplesLeft--;
        if (windowPosition + windowSize == BATCH_SIZE - 1) {
            numOfSamples = powerComputer.readBatch(array2);
            SamplesLeft += numOfSamples;
        }
        if (windowPosition == BATCH_SIZE - 1){
            switchArray();
            windowPosition = 0;
        }


    }

    public void advanceBy(int offset) throws IOException{
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
