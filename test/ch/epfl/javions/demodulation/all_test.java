package ch.epfl.javions.demodulation;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.*;

public class all_test {
    @Test
    void readBatchWorks() throws IOException {
        File initialFile = new File("resources/samples.bin");
        try {
            short[] tab = new short[4804];
            InputStream stream = new FileInputStream(initialFile);
            SamplesDecoder decode = new SamplesDecoder(stream, 4804);
            decode.readBatch(tab);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testsIfConstructorWorks() throws IOException {
        File initialFile = new File("resources/samples.bin");
        try {
            short[] tab = new short[4804];
            InputStream stream = new FileInputStream(initialFile);
            SamplesDecoder decode = new SamplesDecoder(stream, 4804);
            decode.readBatch(tab);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }




    }
    @Test
    void readBatchTest() throws IOException {
        short[] expected = new short[]{-3, 8 ,-9 ,-8, -5 ,-8, -12, -16, -23 ,-9};
        short[] actual = new short[1200];


        InputStream stream = new FileInputStream("resources/samples.bin");
        SamplesDecoder a = new SamplesDecoder(stream,1200);
        int b = a.readBatch(actual);
        for (int i = 0; i < 10; i++) {
            assertEquals(expected[i],actual[i]);
        }
    }


    @Test
    void readBatchWorksWithLicitArguments() throws IOException{
        String s = getClass().getResource("/samples.bin").getFile();
        s = URLDecoder.decode(s, UTF_8);
        InputStream stream = new FileInputStream(s);
        PowerComputer decoder = new PowerComputer(stream,1208);
        int[] batch = new int[1208];
        decoder.readBatch(batch);
        // int [] expected = new int[]{73, 292, 65, 745, 98, 4226, 12244,
        //25722, 36818, 23825};
        for (int i = 0; i <1208 ; i++) {
            //assertEquals(expected[i],batch[i]);
        }
    }


    @Test
    void constructorThrowsNegativeSize() throws IOException {
        String testFile = getClass().getResource("/samples.bin").getFile();
        testFile = URLDecoder.decode(testFile, UTF_8);
        try(InputStream stream = new FileInputStream(testFile)) {
            assertThrows(IllegalArgumentException.class, () -> new PowerComputer(stream, -1));
        }
    }


    @Test
    void constructorThrowsIllegalSize() throws IOException {
        String testFile = getClass().getResource("/samples.bin").getFile();
        testFile = URLDecoder.decode(testFile, UTF_8);
        try(InputStream stream = new FileInputStream(testFile)) {
            assertThrows(IllegalArgumentException.class, () -> new PowerComputer(stream, 9));
        }
    }


    @Test
    void readBatchFailsWithIllicitArgument() throws IOException{
        String testFile = getClass().getResource("/samples.bin").getFile();
        testFile = URLDecoder.decode(testFile, UTF_8);
        InputStream stream = new FileInputStream(testFile);
        PowerComputer powerComputer = new PowerComputer(stream, 16);
        int[] testArray = new int[6];


        assertThrows(IllegalArgumentException.class, () -> powerComputer.readBatch(testArray));
    }


    @Test
    public void testIsFull() throws IOException {
        InputStream stream = new ByteArrayInputStream(new byte[40]);
        /** 40 représente la taille d'un flot d'octet en entrée,
         * dont découlent 20 échantillons, dont découlent 10 échantillons de puissance
         * donc décaler de 5 la fenêtre la laisse remplie, puis la redécaler entraine qu'elle n'est plus remplie*/
        int windowSize = 5;
        PowerWindow window = new PowerWindow(stream, windowSize);
        window.advanceBy(5);
        assertTrue(window.isFull());


        window.advance();
        assertFalse(window.isFull());


    }


    @Test
    public void testGetValidIndex() throws IOException {
        //Mettre BatchSize à 8 pour tester le changement de tableau, le changement d'index
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
    void isFullIfTheWindowIsOn2Batches() throws IOException {//je change les tableaux à une taille de 800
        List<Byte> bytes = new ArrayList<>();
        for (int i = 0; i < (Math.scalb(1,16) -600) * 4; i+=2) {
            bytes.add((byte) 0);
            bytes.add((byte) 8);
        }
        InputStream stream = new FileInputStream("resources/samples.bin");
        byte[] bytes1 = new byte[4804];


        int readBatch = stream.readNBytes(bytes1,0,bytes1.length);


        for (int i = 0; i < bytes1.length; i++) {
            bytes.add(bytes1[i]);
        }
        byte[] bytesFinal = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            bytesFinal[i]=bytes.get(i);
        }
        InputStream stream2 = new ByteArrayInputStream(bytesFinal);
        PowerWindow powerWindow = new PowerWindow(stream2,5);
        for (int i = 0; i < Math.scalb(1,16) +597; i++) {
            powerWindow.advance();
            if(i== Math.scalb(1,16)-5 ){
                powerWindow.isFull();
            }
        }
        assertFalse(powerWindow.isFull());
    }


    @Test
    void getWorksWithTableIntersection() throws IOException {
        String d = getClass().getResource("/samples.bin").getFile();
        d = URLDecoder.decode(d, UTF_8);
        InputStream file = new FileInputStream(d);
        PowerWindow powerWindow = new PowerWindow(file, 2);
        powerWindow.advanceBy(8);
        assertEquals(23825, powerWindow.get(1));
        file.close();
    }


    @Test
    void getWorksWithSingleTable() throws IOException {
        String d = getClass().getResource("/samples.bin").getFile();
        d = URLDecoder.decode(d, UTF_8);
        InputStream file = new FileInputStream(d);
        PowerWindow powerWindow = new PowerWindow(file, 2);
        powerWindow.advance();
        assertEquals(65, powerWindow.get(1));
        file.close();
    }




    @Test
    void isFullWorksWithLastValidElement() throws IOException {
        String testFile = getClass().getResource("/samples.bin").getFile();
        testFile = URLDecoder.decode(testFile, UTF_8);
        try (InputStream file = new FileInputStream(testFile)) {
            PowerWindow powerWindow = new PowerWindow(file, 2);
            powerWindow.advanceBy(1198);
            assertTrue(powerWindow.isFull());
        }
    }


    @Test
    void isFullWorksWithInvalidElement() throws IOException {
        String testFile = getClass().getResource("/samples.bin").getFile();
        testFile = URLDecoder.decode(testFile, UTF_8);
        try (InputStream file = new FileInputStream(testFile)) {
            PowerWindow powerWindow = new PowerWindow(file, 2);
            powerWindow.advanceBy(1199);
            assertTrue(powerWindow.isFull());
        }
    }


    @Test
    void constructorFailsWithInvalidWindowSize() throws IOException {
        String testFile = getClass().getResource("/samples.bin").getFile();
        testFile = URLDecoder.decode(testFile, UTF_8);
        try(InputStream stream = new FileInputStream(testFile)) {
            assertThrows(IllegalArgumentException.class, () -> new PowerWindow(stream, (int) Math.pow(2, 16) + 1));
        }
    }


    @Test
    void getFailsWithIllicitIndex() throws IOException{
        String testFile = getClass().getResource("/samples.bin").getFile();
        testFile = URLDecoder.decode(testFile, UTF_8);
        try(InputStream stream = new FileInputStream(testFile)) {
            PowerWindow powerWindow = new PowerWindow(stream, 2);
            assertThrows(IndexOutOfBoundsException.class, () -> powerWindow.get(3));
        }
    }




}
