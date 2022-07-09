package com.zeldaspeedruns.zeldaspeedruns.security.user;

import com.zeldaspeedruns.zeldaspeedruns.security.SecureTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class RecoveryTokenTests {
    private RecoveryToken token;

    @BeforeEach
    void beforeEach() {
        var tokenValue = SecureTokenUtils.generateAlphanumericToken(RecoveryToken.TOKEN_VALUE_LENGTH);
        var user = ZsrUserTestUtils.zsrUser("username");
        token = new RecoveryToken(user, tokenValue);
    }

    @Test
    void constructor_whenArgsNull_throwsException() {
        assertThrows(NullPointerException.class, () -> new RecoveryToken(null, null));
        assertThrows(NullPointerException.class, () -> new RecoveryToken(null, null, null));
    }

    @Test
    void constructor_instantiatesRecoveryToken() {
        var user = new ZsrUser("tester", "test@example.com","password");
        var expiresAt =  Instant.now().plus(1, ChronoUnit.MINUTES);
        var token = new RecoveryToken(user, "random-value", expiresAt);

        assertNull(token.getId());
        assertEquals(expiresAt, token.getExpiresAt());
        assertEquals(user, token.getUser());
        assertEquals("random-value", token.getTokenValue());
    }

    @Test
    void getUuid_isNotNull() {
        assertNotNull(token.getUuid());
    }

    @Test
    void setUser_whenNull_throwsException() {
        assertThrows(NullPointerException.class, () -> token.setUser(null));
    }

    @Test
    void setUser_valueIsSet() {
        var newUser = ZsrUserTestUtils.zsrUser("tester");
        token.setUser(newUser);
        assertEquals(newUser, token.getUser());
    }

    @Test
    void setTokenValue_whenNull_throwsException() {
        assertThrows(NullPointerException.class, () -> token.setTokenValue(null));
    }

    @Test
    void setTokenValue_valueIsSet() {
        var newValue = SecureTokenUtils.generateAlphanumericToken(RecoveryToken.TOKEN_VALUE_LENGTH);
        token.setTokenValue(newValue);
        assertEquals(newValue, token.getTokenValue());
    }

    @Test
    void getExpiresAt_whenDefault_isInFuture() {
        assertTrue(token.getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void setExpiresAt_whenNull_throwsException() {
        assertThrows(NullPointerException.class, () -> token.setExpiresAt(null));
    }

    @Test
    void setExpiresAt_valueIsSet() {
        var newExpiryDate = Instant.now().plus(1, ChronoUnit.DAYS);
        token.setExpiresAt(newExpiryDate);
        assertEquals(newExpiryDate, token.getExpiresAt());
    }

    @Test
    void toString_containsTokenValue() {
        assertTrue(token.toString().contains(token.getTokenValue()));
    }
}
