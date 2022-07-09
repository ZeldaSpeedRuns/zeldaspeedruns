package com.zeldaspeedruns.zeldaspeedruns.security.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ZsrUserTests {
    private ZsrUser user;

    @BeforeEach
    void beforeEach() {
        this.user = ZsrUserTestUtils.zsrUser("tester");
    }

    @Test
    void constructor_whenArgsNull_throwsException() {
        assertThrows(NullPointerException.class, () -> {
            new ZsrUser(null, null, null);
        });
    }

    @Test
    void getUuid_isNotNull() {
        assertNotNull(user.getUuid());
    }

    @Test
    void setUsername_whenUsernameNull_throwsException() {
        var e = assertThrows(NullPointerException.class, () -> user.setUsername(null));

        assertEquals(e.getMessage(), "username must not be null");
    }

    @Test
    void setEmailAddress_whenEmailAddressNull_throwsException() {
        var e = assertThrows(NullPointerException.class, () -> user.setEmailAddress(null));

        assertEquals(e.getMessage(), "email address must not be null");
    }

    @Test
    void setPassword_whenPasswordNull_throwsException() {
        var e = assertThrows(NullPointerException.class, () -> user.setPassword(null));

        assertEquals(e.getMessage(), "password must not be null");
    }

    @Test
    void setUsername_setsNewValue() {
        user.setUsername("new_username");
        assertEquals(user.getUsername(), "new_username");
    }

    @Test
    void setEmailAddress_setsNewValue() {
        user.setEmailAddress("new@example.com");
        assertEquals(user.getEmailAddress(), "new@example.com");
    }

    @Test
    void setPassword_setsNewValue() {
        user.setPassword("new_password");
        assertEquals(user.getPassword(), "new_password");
    }

    @Test
    void setEnabled_setsValue() {
        user.setEnabled(false);
        assertFalse(user.isEnabled());
    }

    @Test
    void setAdministrator_setsValue() {
        user.setAdministrator(true);
        assertTrue(user.isAdministrator());
    }
}
