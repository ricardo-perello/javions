package ch.epfl.javions.adsb;

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
        int sumPics = powerWindow.get(0) + powerWindow.get(10) + powerWindow.get(35) + powerWindow.get(45);
        int nextSumPics = powerWindow.get(1) + powerWindow.get(11) + powerWindow.get(36) + powerWindow.get(46);
        int sumValleys = powerWindow.get(5) + powerWindow.get(15) + powerWindow.get(20)
                        + powerWindow.get(25) + powerWindow.get(30) + powerWindow.get(40);
        if((sumPics >= 2 * sumValleys) && (sumPics > previousSumPics) && (sumPics > nextSumPics)){
            //no se como hacer
            //tenemos que decalarlo
            // tenemos que desde el powerWindow coger todos los 1200
            // demodulado
            //comparar con DF
            byte[] Df = new byte[8];
            for (int i = 0; i <8; i++) {
                if((powerWindow.get(80 + (10 * i))) < powerWindow.get(85 + (10 * i))){
                    Df[i] = 0;
                }else{
                    Df[i] = 1;
                }
            }
            //meter en un tablo
            //hacer rawMessage of(...)



            previousSumPics = 0;
            powerWindow.advanceBy(1200);
        }else{
            previousSumPics = sumPics;
            powerWindow.advance();
            return null;
        }

    }

}
