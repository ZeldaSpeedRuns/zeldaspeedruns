package com.zeldaspeedruns.zeldaspeedruns.security.userdetails;

import com.zeldaspeedruns.zeldaspeedruns.organizations.Organization;
import com.zeldaspeedruns.zeldaspeedruns.organizations.OrganizationMember;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

public class ZsrUserDetails implements UserDetails {
    private final ZsrUser user;

    public ZsrUserDetails(ZsrUser user) {
        this.user = Objects.requireNonNull(user, "user must not be null");
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (user.isSuperuser()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    public ZsrUser getUser() {
        return user;
    }
}
