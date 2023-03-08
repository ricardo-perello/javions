package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {
    final static int POW16 = (int) Math.pow(2, 16);
    final static int POW17 = (int) Math.pow(2, 17);
    private InputStream stream;
    private int windowSize;
    private int windowPosition;
    private PowerComputer powerComputer;
    private int[] even = new int[POW16];
    private int[] odd = new int[POW16];
    private int[] currentArray = even;
    boolean isEvenTable = true;
    private int numOfSamples;

//todo add comments
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument((windowSize > 0) && (windowSize <= POW16));
        this.stream = stream;
        this.windowSize = windowSize;
        powerComputer = new PowerComputer(stream, POW17);
        numOfSamples = powerComputer.readBatch(even);
    }

    public int size(){return windowSize;}

    public long position(){return windowPosition;}

    public boolean isFull() {return (windowPosition+windowSize < numOfSamples);}

    public int get(int i){
        if (!((i >= 0)&&(i < windowSize))){
            throw new IndexOutOfBoundsException();
        }
        if (windowPosition + i < numOfSamples){
            return currentArray[windowPosition+i];
        }
        else{
            int newIndex = numOfSamples-(windowPosition+i);
            switchArray(isEvenTable);
            return currentArray[newIndex];
        }
    }

    public void advance() throws IOException{
        windowPosition++;
        if (windowPosition + windowSize > numOfSamples) {
            switchArray(isEvenTable);
            windowPosition = 0;
            numOfSamples = powerComputer.readBatch(currentArray);
        }

    }

    public void advanceBy(int offset) throws IOException{
        for (int i = 0; i < offset; i++) {
            advance();
        }
    }
    private void switchArray(boolean b){
        if (b){
            b = false;
            currentArray = odd;
        }
        else{
            b = true;
            currentArray = even;
        }
    }

}
