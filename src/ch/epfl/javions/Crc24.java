package ch.epfl.javions;

import static ch.epfl.javions.Bits.extractUInt;

public final class Crc24 {
    public static int GENERATOR = 0xFFF409;
    private final int generator;
    private static int mask = 16_777_215;
    byte[] Crc24Table;
    public Crc24 (int generator){
        this.generator = generator;
    }

    public static int crc_bitwise(int generator, byte[] bytes){
        int[] table = {0, generator & mask};
        int crc = 0;
        int byteIndex;
        int bitIndex;
        for(byte b: bytes){
            for(int i=7;i>=0;i--){
                crc = ((crc << 1) | extractUInt(b,i,1))^ table[extractUInt(crc, 23, 1)];
            }
        }
        for(int i = 0; i < 4; i++){
            for(int j = 7; j >= 0;j--){
                crc = ((crc << 1)) ^ table[extractUInt(crc, 23, 1)];
            }
        }
        crc = crc & mask;
        return crc;
    }


    public int crc_Basic (){
        return crc_bitwise(generator, Crc24Table);
    }

    private static int[] buildTable(int generator){
        int [] table = new int[256];
        for (int i = 0; i < 256; i++) {
            table[i] = crc_bitwise(generator, new byte[] {(byte) i});
        }
        return table;
    }
}
