package com.zeldaspeedruns.zeldaspeedruns.security.userdetails;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import static org.junit.jupiter.api.Assertions.*;

public class ZsrUserDetailsTests {
    private ZsrUser user;

    private ZsrUserDetails userDetails;

    @BeforeEach
    void beforeEach() {
        user = ZsrUserTestUtils.zsrUser("username");
        userDetails = new ZsrUserDetails(user);
    }

    @Test
    void constructor_instantiatesValidObject() {
        var ud = new ZsrUserDetails(user);
        assertEquals(user, ud.getUser());
    }

    @Test
    void getAuthorities_containsUserRole() {
        GrantedAuthority userRole = new SimpleGrantedAuthority("ROLE_USER");
        assertTrue(userDetails.getAuthorities().contains(userRole));
    }

    @Test
    void getAuthorities_whenAdministrator_containsAdminRole() {
        user.setAdministrator(true);
        GrantedAuthority adminRole = new SimpleGrantedAuthority("ROLE_ADMIN");
        assertTrue(userDetails.getAuthorities().contains(adminRole));
    }

    @Test
    void getAuthorities_whenNotAdministrator_doesNotContainAdminRole() {
        user.setAdministrator(false);
        GrantedAuthority adminRole = new SimpleGrantedAuthority("ROLE_ADMIN");
        assertFalse(userDetails.getAuthorities().contains(adminRole));
    }

    @Test
    void getPassword_returnsUserPassword() {
        assertEquals(user.getPassword(), userDetails.getPassword());
    }

    @Test
    void getUsername_returnsUserUsername() {
        assertEquals(user.getUsername(), userDetails.getUsername());
    }

    @Test
    void isEnabled_whenUserEnabled_isTrue() {
        user.setEnabled(true);
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void isAccountNonExpired_isTrue() {
        assertTrue(userDetails.isAccountNonExpired());
    }

    @Test
    void isAccountNonLocked_isTrue() {
        assertTrue(userDetails.isAccountNonLocked());
    }

    @Test
    void isCredentialsNonExpired_isTrue() {
        assertTrue(userDetails.isCredentialsNonExpired());
    }

    @Test
    void isEnabled_whenUserDisabled_isFalse() {
        user.setEnabled(false);
        assertFalse(userDetails.isEnabled());
    }

}
