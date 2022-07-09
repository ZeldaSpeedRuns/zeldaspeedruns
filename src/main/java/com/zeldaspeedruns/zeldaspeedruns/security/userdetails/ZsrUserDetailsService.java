package com.zeldaspeedruns.zeldaspeedruns.security.userdetails;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ZsrUserDetailsService implements UserDetailsService {
    private final ZsrUserService userService;

    public ZsrUserDetailsService(ZsrUserService userService) {
        this.userService = userService;
    }

    @Override
    public ZsrUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userService.loadUserByUsername(username);
        return new ZsrUserDetails(user);
    }
}
