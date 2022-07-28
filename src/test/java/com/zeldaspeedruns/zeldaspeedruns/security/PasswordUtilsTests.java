package com.zeldaspeedruns.zeldaspeedruns.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PasswordUtilsTests {
    @Test
    void generatePassword() {
        var password = PasswordUtils.generatePassword(40);
        assertEquals(40, password.length());
    }

    @Test
    void generatePassword_whenLengthIsNegative_throwsException() {
        assertThrows(RuntimeException.class, () -> PasswordUtils.generatePassword(0));
        assertThrows(RuntimeException.class, () -> PasswordUtils.generatePassword(-1));
    }
}