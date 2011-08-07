package com.cyrilpottiers.androlib.arrays;

import java.lang.reflect.Array;

public class ArraysUtils {

    @SuppressWarnings("unchecked")
    public static <T> T[] arrayexpend(T[] array, int increment) {
        System.arraycopy(array, 0, array = (T[]) Array.newInstance(array.getClass().getComponentType(), array.length
            + increment), 0, array.length - increment);
        return array;
    }

    public static int[] arrayintexpend(int[] array, int increment) {
        System.arraycopy(array, 0, array = new int[array.length + increment], 0, array.length
            - increment);
        return array;
    }

    public static byte[] arraybyteexpend(byte[] array, int increment) {
        System.arraycopy(array, 0, array = new byte[array.length + increment], 0, array.length
            - increment);
        return array;
    }

    public static <T> T[] arrayappend(T[] array, T[] append) {
        return arrayappend(array, append, append.length);
    }

    public static int[] arrayintappend(int[] array, int[] append) {
        return arrayintappend(array, append, append.length);
    }

    public static byte[] arraybyteappend(byte[] array, byte[] append) {
        return arraybyteappend(array, append, append.length);
    }

    public static <T> T[] arrayappend(T[] array, T[] append, int size) {
        array = arrayexpend(array, size);
        System.arraycopy(append, 0, array, array.length - size, size);
        return array;
    }

    public static int[] arrayintappend(int[] array, int[] append, int size) {
        array = arrayintexpend(array, size);
        System.arraycopy(append, 0, array, array.length - size, size);
        return array;
    }

    public static byte[] arraybyteappend(byte[] array, byte[] append, int size) {
        array = arraybyteexpend(array, size);
        System.arraycopy(append, 0, array, array.length - size, size);
        return array;
    }

    public static boolean equals(byte[] array1, byte[] array2) {
        if ((array1 == null && array2 != null)
            || (array1 != null && array2 == null))
            return false;
        else if (array1 != null && array2 != null) {
            if (array1.length != array2.length) return false;
            for (int i = array1.length; --i >= 0;)
                if (array1[i] != array2[i]) return false;
        }
        return true;
    }
}
