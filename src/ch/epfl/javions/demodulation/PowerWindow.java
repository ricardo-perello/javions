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
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument((windowSize > 0) && (windowSize <= BATCH_SIZE));
        this.stream = stream;
        this.windowSize = windowSize;
        powerComputer = new PowerComputer(stream, BATCH_SIZE);
        numOfSamples = powerComputer.readBatch(array1);
        SamplesLeft = numOfSamples;
    }

    public int size(){return windowSize;}

    public long position(){return absoluteWindowPosition;}

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
        if (windowPosition + windowSize == BATCH_SIZE) {
            numOfSamples = powerComputer.readBatch(array2);
            SamplesLeft += numOfSamples;
        }
        if (windowPosition == BATCH_SIZE){
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
        /*
        if (b){
            b = false;
            currentArray = array2;
        }
        else{
            b = true;
            currentArray = array1;
        }

         */
        int[] tempArray = array2;
        array2 = array1;
        array1 = tempArray;
    }

}
