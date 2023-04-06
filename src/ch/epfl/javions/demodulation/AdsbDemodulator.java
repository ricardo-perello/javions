package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {
    private static final int LENGTH_OF_WINDOW = 1200;
    private static final int LENGTH_RAWMESSAGE = 14;
    private static final int LENGTH_BYTE = 8;
    private static final int DISTANCE_SAMPLES = 10;

    private static final int MULTIPLIER_FROM_MICRO_TO_NANO = 100;
    private static final int LENGTH_BYTE_TIME_SCALED = LENGTH_BYTE * DISTANCE_SAMPLES;
    private static final int NUMBER_SAMPLES_PREAMBLE = LENGTH_BYTE * DISTANCE_SAMPLES;
    private static final int OFFSET = 5;

    private static final int[] POSITION_SAMPLES_CURRENT_SUM_PICS = {0, 10, 35, 45};
    private static final int[] POSITION_SAMPLES_SUM_VALLEYS = {5, 15, 20, 25, 30, 40};

    private final PowerWindow powerWindow;

    /**
     * constructor for AdsbDemodulator
     *
     * @param samplesStream, InputStream, document from where we are going to extract the values
     * @throws IOException error if mistake in entry
     */
    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        powerWindow = new PowerWindow(samplesStream, LENGTH_OF_WINDOW);
    }
    /**
     * method that demodulates the stream
     *
     * @return raw message or null if window is not full
     * @throws IOException error from input or output
     */
    public RawMessage nextMessage() throws IOException {
        int sumPics = powerWindow.get(POSITION_SAMPLES_CURRENT_SUM_PICS[0]) +
                powerWindow.get(POSITION_SAMPLES_CURRENT_SUM_PICS[1]) +
                powerWindow.get(POSITION_SAMPLES_CURRENT_SUM_PICS[2]) +
                powerWindow.get(POSITION_SAMPLES_CURRENT_SUM_PICS[3]);

        int previousSumPics = 0;

        while (powerWindow.isFull()) {
            int nextSumPics = powerWindow.get(POSITION_SAMPLES_CURRENT_SUM_PICS[0] + 1) +
                    powerWindow.get(POSITION_SAMPLES_CURRENT_SUM_PICS[1] + 1) +
                    powerWindow.get(POSITION_SAMPLES_CURRENT_SUM_PICS[2] + 1) +
                    powerWindow.get(POSITION_SAMPLES_CURRENT_SUM_PICS[3] + 1);

            if ((sumPics > previousSumPics) && (sumPics > nextSumPics)) {
                int sumValleys = powerWindow.get(POSITION_SAMPLES_SUM_VALLEYS[0])
                        + powerWindow.get(POSITION_SAMPLES_SUM_VALLEYS[1])
                        + powerWindow.get(POSITION_SAMPLES_SUM_VALLEYS[2])
                        + powerWindow.get(POSITION_SAMPLES_SUM_VALLEYS[3])
                        + powerWindow.get(POSITION_SAMPLES_SUM_VALLEYS[4])
                        + powerWindow.get(POSITION_SAMPLES_SUM_VALLEYS[5]);


                if (sumPics >= (2 * sumValleys)) {
                    byte[] bytes = new byte[LENGTH_RAWMESSAGE];

                    //this allows us to only calculate the length of the rawMessage
                    byteDemodulator(0, 1,  bytes);

                    if (RawMessage.size(bytes[0]) == LENGTH_RAWMESSAGE) {
                        //we put startFirstLoop at 1 because, we know already for 0 by the previous one
                        byteDemodulator(1, LENGTH_RAWMESSAGE, bytes);

                        RawMessage result = RawMessage.of(powerWindow.position() * MULTIPLIER_FROM_MICRO_TO_NANO, bytes);

                        if (result != null) {
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
     * this private method allows to determine bits based on the power Window determine if the bit is a 0 or a 1
     *
     * @param startFirstLoop,  int, beginning of the first fori loop
     * @param endFirstLoop,    int, end of the first fori loop
     * @param bytes,           byte[], table where we place the value of the signal
     */
    private void byteDemodulator(int startFirstLoop, int endFirstLoop, byte[] bytes) {
        for (int i = startFirstLoop; i < endFirstLoop; i++) {
            for (int j = 0; j < LENGTH_BYTE; j++) {
                if (powerWindow.get(NUMBER_SAMPLES_PREAMBLE + (LENGTH_BYTE_TIME_SCALED * i) + (DISTANCE_SAMPLES * j)) >=
                        powerWindow.get(NUMBER_SAMPLES_PREAMBLE + OFFSET + (LENGTH_BYTE_TIME_SCALED * i) + (DISTANCE_SAMPLES * j))) {
                    bytes[i] |= (byte) (1 << (LENGTH_BYTE - 1 - j));
                }
            }
        }
    }
}
