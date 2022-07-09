package com.zeldaspeedruns.zeldaspeedruns.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SecureTokenUtilsTests {
    @Test
    void whenLengthIsZero_throwsRuntimeException() {
        var e = assertThrows(RuntimeException.class, () -> SecureTokenUtils.generateAlphanumericToken(0));
        assertEquals("length must be positive integer", e.getMessage());
    }

    @Test
    void whenLengthIsNegative_throwsRuntimeException() {
        var e = assertThrows(RuntimeException.class, () -> SecureTokenUtils.generateAlphanumericToken(-2));
        assertEquals("length must be positive integer", e.getMessage());
    }

    @Test
    void whenLengthIsPositive_generatesString() {
        String generatedString = SecureTokenUtils.generateAlphanumericToken(40);
        assertNotNull(generatedString);
        assertEquals(40, generatedString.length());
    }
}
