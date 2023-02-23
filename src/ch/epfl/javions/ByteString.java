package ch.epfl.javions;

import java.util.HexFormat;

public final class ByteString{
    public final byte[] octetTable;

    public ByteString(byte[] bytes){
        octetTable = new byte[]
    }

    public ByteString ofHexadecimalString(String hexString){
        Preconditions.checkArgument(((hexString.length())%2) == 0);
        // TODO: 23/02/2023 NumberFormatException
        HexFormat hf = HexFormat.of().withUpperCase();
        return new ByteString(hf.parseHex(hexString));
    }

    public int size(){
        return octetTable.length;
    }

    public int byteAt(int index){
        if (index > size()){
            throw new IndexOutOfBoundsException();
        }
        return octetTable[index];
    }

    public long bytesInRange(int fromIndex, int toIndex){
        long output =
    }

}
