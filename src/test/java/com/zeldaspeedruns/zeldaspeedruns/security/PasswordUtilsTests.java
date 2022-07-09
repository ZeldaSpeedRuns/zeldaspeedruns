package com.zeldaspeedruns.zeldaspeedruns.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PasswordUtilsTests {
    @Test
    void whenLengthIsZero_throwsRuntimeException() {
        var e = assertThrows(RuntimeException.class, () -> PasswordUtils.generatePassword(0));
        assertEquals("length must be positive integer", e.getMessage());
    }

    @Test
    void whenLengthIsNegative_throwsRuntimeException() {
        var e = assertThrows(RuntimeException.class, () -> PasswordUtils.generatePassword(-2));
        assertEquals("length must be positive integer", e.getMessage());
    }

    @Test
    void whenLengthIsPositive_generatesString() {
        String generatedPassword = PasswordUtils.generatePassword(40);
        assertNotNull(generatedPassword);
        assertEquals(40, generatedPassword.length());
    }
}
