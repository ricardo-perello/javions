package ch.epfl.javions;

import java.util.HexFormat;

public final class ByteString{
    byte[] octetTable;
    /**
     * Creates an array of bytes that is final and unsigned
     * @param bytes is byte array
     */
    public ByteString(byte[] bytes){ //todo check if final
        byte[] octetTableCopy = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++){
            octetTableCopy[i] = (byte) Byte.toUnsignedInt(bytes[i]);
            octetTable = octetTableCopy.clone();
        }
    }

    public ByteString ofHexadecimalString(String hexString){
        Preconditions.checkArgument(((hexString.length())%2) == 0);
        // TODO: 23/02/2023 NumberFormatException
        HexFormat hf = HexFormat.of().withUpperCase();
        return new ByteString(hf.parseHex(hexString));
    }

    //public int size(){
        //return octetTable.length;
    //}

    /**public int byteAt(int index){
        if (index > size()){
            throw new IndexOutOfBoundsException();
        }
        return octetTable[index];
    }**/

    //public long  bytesInRange(int fromIndex, int toIndex){

    //}


}
