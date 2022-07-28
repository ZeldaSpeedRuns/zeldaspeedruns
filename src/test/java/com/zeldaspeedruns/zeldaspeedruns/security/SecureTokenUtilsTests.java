package com.zeldaspeedruns.zeldaspeedruns.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecureTokenUtilsTests {
    @Test
    void generateAlphanumericToken() {
        var s = SecureTokenUtils.generateAlphanumericToken(40);
        assertEquals(40, s.length());
    }

    @Test
    void generateAlphanumericToken_whenLengthIsNegative_throwsException() {
        assertThrows(RuntimeException.class, () -> SecureTokenUtils.generateAlphanumericToken(0));
        assertThrows(RuntimeException.class, () -> SecureTokenUtils.generateAlphanumericToken(-1));
    }
}