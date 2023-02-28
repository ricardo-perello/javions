package ch.epfl.javions;

import static ch.epfl.javions.Bits.extractUInt;

public final class Crc24 {
    final static int GENERATOR = 0xFFF409;
    final static int mask = 16_777_215;
    private final int generator;
    public Crc24(int generator){
        this.generator = generator & mask;
    }
    int crc_bitwise(byte[] bytes, int generator){
        int[] table = {0, generator & mask};
        int crc = 0;
        int byteIndex;
        int bitIndex;
        for(byte b: bytes){
            for(int i=7;i>=0;i--){
                crc = ((crc << 1) | Bits.extractUInt(b,i,1))^ table[extractUInt(crc, 23, 1)];
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
}
