package ch.epfl.javions.demodulation;

import ch.epfl.javions.ByteString;
import ch.epfl.javions.adsb.RawMessage;
import ch.epfl.javions.demodulation.PowerWindow;

import java.io.IOException;
import java.io.InputStream;

public final class AdsbDemodulator {
    private PowerWindow powerWindow;
    private int previousSumPics = 0;
    public AdsbDemodulator (InputStream samplesStream) throws IOException{
        powerWindow = new PowerWindow(samplesStream, 1200);
    }

    public RawMessage nextMessage() throws IOException{
        while(powerWindow.isFull()) {
            int sumPics = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);
            int nextSumPics = powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);
            int sumValleys = powerWindow.get(5) + powerWindow.get(15) + powerWindow.get(20)
                    + powerWindow.get(25) + powerWindow.get(30) + powerWindow.get(40);
            if ((sumPics > previousSumPics) && (sumPics > nextSumPics)) {
                if (sumPics >= 2 * sumValleys) {
                    //no se como hacer
                    //tenemos que decalarlo
                    // tenemos que desde el powerWindow coger todos los 1200
                    // demodulado
                    //comparar con DF
                    byte[] bytes = new byte[14];
                    for (int i = 0; i < 8; i++) {
                        if (!((powerWindow.get(80 + (10 * i))) < powerWindow.get(85 + (10 * i)))) {
                            bytes[0] = (byte) (bytes[0] & 1 << (7 - i));
                        }
                    }
                    ByteString byteString = new ByteString(bytes);
                    RawMessage rawMessage = new RawMessage(powerWindow.position() * 100, byteString);
                    if (rawMessage.downLinkFormat() == 17) {
                        for (int i = 1; i < 14; i++) {
                            for (int j = 0; j < 8; j++) {
                                if (!((powerWindow.get(80 + (80 * i) + (10 * j))) < powerWindow.get(85 + (80 * i) + (10 * j)))) {
                                    bytes[i] = (byte) (bytes[i] | 1 << (7 - j));
                                }
                            }
                        }
                    }
                    RawMessage newRawMessage = RawMessage.of(powerWindow.position() * 100, bytes);
                    //meter en un tablo
                    //hacer rawMessage of(...)
                    previousSumPics = 0;
                    powerWindow.advanceBy(1200);
                    return newRawMessage;
                }
                else {
                    previousSumPics = sumPics;
                    powerWindow.advanceBy(1);
                }
            } else {
                previousSumPics = sumPics;
                powerWindow.advanceBy(1);
            }
        }
        return null;
    }
}
