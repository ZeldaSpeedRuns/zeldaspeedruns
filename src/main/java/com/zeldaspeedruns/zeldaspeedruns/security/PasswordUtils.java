package com.zeldaspeedruns.zeldaspeedruns.security;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

/**
 * Utilities for generating passwords.
 */
public class PasswordUtils {
    private final static PasswordGenerator generator = new PasswordGenerator();

    /**
     * Generates a random, secure user password containing at least two numbers and a mix of lower and uppercase
     * characters.
     *
     * @param length The length of the generated password.
     * @return The generated password.
     */
    public static String generatePassword(int length) {
        if (length <= 0) {
            throw new RuntimeException("length must be positive integer");
        }

        var alphaRule = new CharacterRule(EnglishCharacterData.Alphabetical);
        var numericalRule = new CharacterRule(EnglishCharacterData.Digit);
        numericalRule.setNumberOfCharacters(2);

        return generator.generatePassword(length, alphaRule, numericalRule);
    }
}
