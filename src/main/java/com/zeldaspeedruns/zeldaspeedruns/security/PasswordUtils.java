package com.zeldaspeedruns.zeldaspeedruns.security;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

public class PasswordUtils {
    private final static PasswordGenerator generator = new PasswordGenerator();

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
