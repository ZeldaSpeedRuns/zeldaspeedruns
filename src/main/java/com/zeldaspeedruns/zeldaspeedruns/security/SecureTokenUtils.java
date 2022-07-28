package com.zeldaspeedruns.zeldaspeedruns.security;


import java.security.SecureRandom;
import java.util.Random;

/**
 * Utilities for generating tokens.
 */
public class SecureTokenUtils {
    private static final Random random = new SecureRandom();

    /**
     * Generates a random, alphanumeric string using a secure RNG.
     *
     * @param length The length of the string, must be greater than 0.
     * @return A random, alphanumeric string.
     */
    public static String generateAlphanumericToken(int length) {
        if (length <= 0) {
            throw new RuntimeException("length must be positive integer");
        }

        return random.ints('0', 'z' + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
