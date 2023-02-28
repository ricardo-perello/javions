package ch.epfl.javions;

import static ch.epfl.javions.Bits.extractUInt;

public final class Crc24 {
    public static int GENERATOR = 0xFFF409;
    private final int generator;
    private static final int mask = 16777215;
    private final int [] table;
    public Crc24 (int generator){
        this.generator = generator & mask;
        table = buildTable(this.generator);
    }

    public int crc(byte[] bytes){
        int crc = 0;
        for (byte b: bytes){
            crc = ((crc << 8)|Byte.toUnsignedInt(b))^table[extractUInt(crc, 16,8 )];
        }
        for (int i=0;i<3;++i){
            crc = ((crc << 8))^table[extractUInt(crc, 16,8 )];
        }
        crc = crc & mask;

        return crc;
    }

    public static int crc_bitwise(int generator, byte[] bytes){
        int[] table = {0,generator & mask};
        int crc = 0;
        for(byte b: bytes){
            for(int i=7;i>=0;i--){
                crc = ((crc << 1) | extractUInt(b,i,1))^ table[extractUInt(crc, 23, 1)];
            }
        }
        for(int i = 0; i < 3; i++){
            for(int j = 7; j >= 0;j--){
                crc = ((crc << 1)) ^ table[extractUInt(crc, 23, 1)];
            }
        }
        crc = crc & mask;
        return crc;
    }
    //~16 000 000 000

    public int crc_Basic (int generator, byte[] bytes){
        return crc_bitwise(generator, bytes);
    }

    private static int[] buildTable(int generator){
        int [] table = new int[256];
        for (int i = 0; i < 256; i++) {
            table[i] = crc_bitwise(generator, new byte[] {(byte) i});
        }
        return table;
    }
}
