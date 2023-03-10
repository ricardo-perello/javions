package ch.epfl.javions.demodulation;
import org.junit.jupiter.api.Test;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

public class PowerWindowTest {
    @Test
    public void testGetValidIndex() throws IOException {
        /**Mettre BatchSize à 8 pour tester le changement de tableau, le changement d'index */
        int[] tab = new int[]{73, 292, 65, 745, 98, 4226, 12244, 25722};
        int[] tab1 = new int[]{36818, 23825, 10730, 1657, 1285, 1280, 394, 521};
        int[] tab2 = new int[]{1370, 200, 292, 290, 106, 116, 194, 64};
        int[] tab3 = new int[]{37, 50, 149, 466, 482, 180, 148, 5576};
        InputStream stream = new FileInputStream("resources/Samples.bin");
        int windowSize = 5;
        PowerWindow window = new PowerWindow(stream, windowSize);

        assertEquals(tab[0], window.get(0));
        assertEquals(tab[1], window.get(1));
        assertEquals(tab[2], window.get(2));
        assertEquals(tab[3], window.get(3));
        assertEquals(tab[4], window.get(4));

        window.advanceBy(8);
        assertEquals(tab1[0], window.get(0));
        assertEquals(tab1[1], window.get(1));
        assertEquals(tab1[2], window.get(2));
        assertEquals(tab1[3], window.get(3));
        assertEquals(tab1[4], window.get(4));

        window.advanceBy(8);
        assertEquals(tab2[0], window.get(0));
        assertEquals(tab2[1], window.get(1));
        assertEquals(tab2[2], window.get(2));
        assertEquals(tab2[3], window.get(3));
        assertEquals(tab2[4], window.get(4));

        window.advanceBy(6);
        assertEquals(tab2[6], window.get(0));
        assertEquals(tab2[7], window.get(1));
        assertEquals(tab3[0], window.get(2));
        assertEquals(tab3[1], window.get(3));
        assertEquals(tab3[2], window.get(4));



    }
    @Test
    public void isFull() throws IOException{
        InputStream stream = new FileInputStream("resources/Samples.bin");
        PowerWindow window1 = new PowerWindow(stream, 6);
        window1.advanceBy(1194);
        assertTrue(window1.isFull());
        window1.advance();
        assertTrue(window1.isFull());
        window1.advance();
        assertFalse(window1.isFull());
    }
    @Test
    void checkGet() throws IOException {

        String d = getClass().getResource("/samples.bin").getFile();
        d = URLDecoder.decode(d, StandardCharsets.UTF_8);
        InputStream file = new FileInputStream(d);
        PowerWindow powerWindow = new PowerWindow(file, 2);
        powerWindow.advanceBy(8);
        assertEquals(23825, powerWindow.get(1));
        file.close();
    }


    @Test
    void checkIsFull() throws IOException {

        String d = getClass().getResource("/samples.bin").getFile();
        d = URLDecoder.decode(d, StandardCharsets.UTF_8);
        InputStream file = new FileInputStream(d);
        PowerWindow powerWindow = new PowerWindow(file, 2);
        powerWindow.advanceBy(1198);
        assertTrue(powerWindow.isFull());
        file.close();


    }


    @Test
    void returnTheGoodWindowSize() throws IOException {

        String d = getClass().getResource("/samples.bin").getFile();
        d = URLDecoder.decode(d, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(d);
        PowerWindow powerWindow = new PowerWindow(stream, 5);
        assertEquals(5, powerWindow.size());

    }


    @Test
    void position() throws IOException {

        String d = getClass().getResource("/samples.bin").getFile();
        d = URLDecoder.decode(d, StandardCharsets.UTF_8);
        InputStream stream = new FileInputStream(d);
        PowerWindow powerWindow = new PowerWindow(stream, 5);
        for (int i = 0; i < 100; i++) {

            assertEquals(i, powerWindow.position());
            powerWindow.advance();

        }

    }


    @Test
    void get() throws IOException {//je change les tableaux à une taille de 8 genre la batchsize des échantillons de puissance

        String d = getClass().getResource("/samples.bin").getFile();
        d = URLDecoder.decode(d, StandardCharsets.UTF_8);

        InputStream stream = new FileInputStream(d);

        PowerWindow powerWindow = new PowerWindow(stream, 3);

        powerWindow.advanceBy(6);

        assertEquals(36818, powerWindow.get(2));

    }


    @Test
    void isFull1() throws IOException {//je change les tableaux à une taille de 800 (batchsize des échantillons de puissances

        String d = getClass().getResource("/samples.bin").getFile();

        d = URLDecoder.decode(d, StandardCharsets.UTF_8);

        InputStream stream = new FileInputStream(d);

        PowerWindow powerWindow = new PowerWindow(stream, 5);

        for (int i = 0; i < 1197; i++) {

            if (i == 797) {

                powerWindow.isFull();

            }

            powerWindow.advance();

        }

        assertFalse(powerWindow.isFull());

    }

}
