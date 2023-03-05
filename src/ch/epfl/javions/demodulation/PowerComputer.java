package ch.epfl.javions.demodulation;

import ch.epfl.javions.Preconditions;

import java.io.InputStream;

public final class PowerComputer {

    private Byte[] PowerComputer;

    public PowerComputer(InputStream stream, int batchSize){
        Preconditions.checkArgument(batchSize > 0);
        Preconditions.checkArgument((batchSize % 8) == 0);
        PowerComputer = new Byte[2 * batchSize];
    }

}
