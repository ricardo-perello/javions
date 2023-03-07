package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.IOException;
import java.io.InputStream;

public final class PowerWindow {
    private InputStream stream;
    private int windowSize;
    private PowerComputer powerComputer;
//todo add comments
    public PowerWindow(InputStream stream, int windowSize) throws IOException {
        Preconditions.checkArgument((windowSize > 0) && (windowSize <= Math.scalb(1, 16)));
        this.stream = stream;
        this.windowSize = windowSize;
        powerComputer = new PowerComputer(stream, windowSize); // TODO: 7/3/23 check if this is correct
    }

    public int size(){return windowSize;}

    public long position(){}

    public boolean isFull() {}

    public int get(int i){
        if (!((i >= 0)&&(i < windowSize))){
            throw new IndexOutOfBoundsException();
        }
    }

    public void advance() throws IOException{}

    public void advanceBy(int offset) throws IOException{}

}
