package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {
    private static final int LENGTH_OF_WINDOW = 1200;
    private static final int LENGTH_RAWMESSAGE = 14;
    private final PowerWindow powerWindow;

    /**
     * constructor for AdsbDemodulator
     *
     * @param samplesStream, InputStream, document from where we are going to extract the values
     * @throws IOException error if mistake in entry
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        powerWindow = new PowerWindow(samplesStream, 1200);
    }
    //todo mirar si simplificable
    /**
     * method that demodulates the stream
     *
     * @return raw message or null if window is not full
     * @throws IOException error from input or output
     */
    public RawMessage nextMessage() throws IOException {
        int sumPics = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);
        int previousSumPics = 0;

        while (powerWindow.isFull()) {
            int nextSumPics = powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);

            if ((sumPics > previousSumPics) && (sumPics > nextSumPics)) {
                int sumValleys = powerWindow.get(5) + powerWindow.get(15) + powerWindow.get(20)
                        + powerWindow.get(25) + powerWindow.get(30) + powerWindow.get(40);

                if (sumPics >= (2 * sumValleys)) {
                    byte[] bytes = new byte[LENGTH_RAWMESSAGE];
                    bitsComputer(0,1,0,8,bytes);

                    if (RawMessage.size(bytes[0]) == LENGTH_RAWMESSAGE) {
                        bitsComputer(1,LENGTH_RAWMESSAGE, 0,8,bytes);

                        RawMessage result = RawMessage.of(powerWindow.position() * 100, bytes);

                        if (result != null) {
                            //previousSumPics = 0;
                            powerWindow.advanceBy(LENGTH_OF_WINDOW);
                            return result;
                        }
                    }
                }
            }
            previousSumPics = sumPics;
            sumPics = nextSumPics;
            powerWindow.advance();
        }
        return null;
    }

    /**
     * this privat method allows to based on the power Window determine if the bit is a 0 or a 1
     * @param startFirstLoop, int, beginning of the first fori loop
     * @param endFirstLoop, int, end of the first fori loop
     * @param startSecondLoop, int, beginning of the second fori loop
     * @param endSecondLoop, int, end of the second fori loop
     * @param bytes, byte[], table where we place the value of the the signal
     */
    private void bitsComputer(int startFirstLoop, int endFirstLoop, int startSecondLoop, int endSecondLoop, byte[] bytes){
        for (int i = startFirstLoop; i < endFirstLoop; i++) {
            for (int j = startSecondLoop; j < endSecondLoop; j++) {
                if (powerWindow.get(80 + (80 * i) + (10 * j)) >=
                        powerWindow.get(85 + (80 * i) + (10 * j))) {
                    bytes[i] |= (byte) (1 << (7 - j));
                }
            }
        }
    }
}
