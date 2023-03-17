package ch.epfl.javions.demodulation;

import ch.epfl.javions.adsb.RawMessage;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {
    private final PowerWindow powerWindow;

    public AdsbDemodulator(InputStream samplesStream) throws IOException {
        powerWindow = new PowerWindow(samplesStream, 1200);
    }

    public RawMessage nextMessage() throws IOException {
        int sumPics = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);
        int previousSumPics = 0;

        while (powerWindow.isFull()) {
            int nextSumPics = powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);

            if ((sumPics > previousSumPics) && (sumPics > nextSumPics)) {
                int sumValleys = powerWindow.get(5) + powerWindow.get(15) + powerWindow.get(20)
                        + powerWindow.get(25) + powerWindow.get(30) + powerWindow.get(40);

                if (sumPics >= (2 * sumValleys)) {
                    byte[] bytes = new byte[14];
                    for (int i = 0; i < 8; ++i) {
                        
                        if ((powerWindow.get(80 + (10 * i))) >= powerWindow.get(85 + (10 * i))) {
                            bytes[0] |= (byte) (1 << (7 - i));
                        }
                    }

                    if (RawMessage.size(bytes[0]) == 14) {
                        for (int i = 1; i < 14; ++i) {
                            for (int j = 0; j < 8; ++j) {

                                if (powerWindow.get(80 + (80 * i) + (10 * j)) >=
                                        powerWindow.get(85 + (80 * i) + (10 * j))) {
                                    bytes[i] |= (byte) (1 << (7 - j));
                                }
                            }
                        }
                        RawMessage result = RawMessage.of(powerWindow.position() * 100, bytes);

                        if (result != null) {
                            //previousSumPics = 0;
                            powerWindow.advanceBy(1200);
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
}
