package com.zeldaspeedruns.zeldaspeedruns.security.userdetails;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;

class ZsrUserDetailsTests {
    private final static String username = "spell";
    private final static String email = "spell@example.com";
    private final static String password = "password";
    private ZsrUser user;
    private ZsrUserDetails userDetails;

    @BeforeEach
    public void beforeEach() {
        user = new ZsrUser(username, email, password);
        userDetails = new ZsrUserDetails(user);
    }

    @Test
    void getAuthorities_hasUserRole() {
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void getAuthorities_whenSuperuser_hasAdminRole() {
        user.setSuperuser(true);
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void getPassword() {
        assertEquals(password, userDetails.getPassword());
    }

    @Test
    void getUsername() {
        assertEquals(username, userDetails.getUsername());
    }

    @Test
    void isAccountNonExpired() {
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked() {
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired() {
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_whenUserEnabled_isTrue() {
        user.setEnabled(true);
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void isEnabled_whenUserDisabled_isFalse() {
        user.setEnabled(false);
        assertFalse(userDetails.isEnabled());
    }

    @Test
    void getUser() {
        assertEquals(user, userDetails.getUser());
    }
}