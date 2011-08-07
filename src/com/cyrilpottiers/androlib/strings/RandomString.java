package com.cyrilpottiers.androlib.strings;

import java.util.Random;

/**
 * <p>
 * Operations for random <code>String</code>s.
 * </p>
 * These are alphanumeric characters that fall between the chars 0-9
 * and the chars a-z.
 * 
 * @author C3PO
 * @since 1.0
 */
public class RandomString {

    private static final char[] symbols = new char[36];

    static {
        for (int idx = 0; idx < 10; ++idx)
            symbols[idx] = (char) ('0' + idx);
        for (int idx = 10; idx < 36; ++idx)
            symbols[idx] = (char) ('a' + idx - 10);
    }

    private final Random        random  = new Random();

    private final char[]        buf;

    /**
     * <p>
     * <code>RandomString</code> instances should be constructed in standard
     * programming.
     * </p>
     * 
     * @param length
     *            the length of the RandomString
     */
    public RandomString(int length) {
        if (length < 1)
            throw new IllegalArgumentException("length < 1: " + length);
        buf = new char[length];
    }

    /**
     * <p>
     * Creates a random string whose length is the number of characters
     * specified.
     * </p>
     * 
     * <p>
     * Characters will be chosen from the set of all characters.
     * </p>
     * 
     * @return the random string
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }

}
