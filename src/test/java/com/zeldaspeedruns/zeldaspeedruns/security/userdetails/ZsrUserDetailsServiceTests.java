package com.zeldaspeedruns.zeldaspeedruns.security.userdetails;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserService;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserTestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ZsrUserDetailsService.class)
public class ZsrUserDetailsServiceTests {
    @MockBean
    private ZsrUserService userService;

    @Autowired
    private ZsrUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_whenFound_returnsUserDetails() {
        when(userService.loadUserByUsername(anyString())).thenAnswer(invocation -> {
            String username = invocation.getArgument(0);
            return ZsrUserTestUtils.zsrUser(username);
        });

        ZsrUserDetails userDetails = userDetailsService.loadUserByUsername("username");
        assertNotNull(userDetails);
        assertEquals("username", userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_whenNotFound_throwsUsernameNotFoundException() {
        when(userService.loadUserByUsername(anyString())).thenThrow(new UsernameNotFoundException("username"));
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("username"));
    }
}
