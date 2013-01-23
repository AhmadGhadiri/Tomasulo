package simulator;

public class Tools{
    //grabs and returns the bits between start and end of value
    //uses big endian bit numbering (high order bit is bit zero)
    public static int grabBits(int value, int start, int end){
        value = value << start;
        value = value >>> start + (31 - end);
        return value;
    }

    //returns value sign extended from signBit to bit 0
    //uses big endian bit numbering
    public static int signExtend(int signBit, int value){
        //least significant bit is bit number 31
        int mask = 1 << (31 - signBit);
        if ((mask & value) != 0){
            mask = 0xffffffff << signBit;
            value = value | mask;
        }
        return value;
    }

    //returns value sign extended from signBit to bit 0
    //uses big endian bit numbering
    public static long signExtend(int signBit, long value){
        long mask = 1 << (31 - signBit);
        if ((mask & value) != 0){
            mask = 0xffffffffffffffffL << signBit;
            value = value | mask;
        }
        return value;
    }

    //pads either the left side or the right side of field with
    //the pad character so that the String returned is len characters
    public static String pad(String field, int len, String padChar,
                             Direction dir){
        int i;
        String padding = padChar;
        int count = len - field.length();
        for (i = 0; i < count - 1; i++) padding = padding + padChar;
        if (count != 0 && dir == Direction.LEFT) field = field + padding;
        if (count != 0 && dir == Direction.RIGHT) field = padding + field;
        return field;
    }
}
