package com.zeldaspeedruns.zeldaspeedruns.security;


import java.security.SecureRandom;
import java.util.Random;

public class SecureTokenUtils {
    private static final Random random = new SecureRandom();

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
