package com.zeldaspeedruns.zeldaspeedruns.security.userdetails;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZsrUserDetailsServiceTests {
    @Mock
    private ZsrUserService userService;

    @InjectMocks
    private ZsrUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername() {
        var user = new ZsrUser("spell", "spell@example.com", "password");

        when(userService.loadByUsername("spell")).thenReturn(user);

        var userDetails = assertDoesNotThrow(() -> userDetailsService.loadUserByUsername("spell"));

        assertNotNull(userDetails);
        assertEquals(user, userDetails.getUser());
    }

    @Test
    void loadUserByUsername_whenNotFound_throwsException() {
        when(userService.loadByUsername(anyString())).thenThrow(new UsernameNotFoundException("not found"));
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("spell"));
    }
}