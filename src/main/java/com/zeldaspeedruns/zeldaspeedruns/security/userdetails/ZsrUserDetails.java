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
    private final Set<OrganizationMember> memberships;

    public ZsrUserDetails(ZsrUser user, Set<OrganizationMember> memberships) {
        this.user = Objects.requireNonNull(user, "user must not be null");
        this.memberships = Objects.requireNonNull(memberships, "memberships must not be null");
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

    public Set<OrganizationMember> getOrganizationMemberships() {
        return memberships;
    }

    public boolean isOrganizationMember(Organization organization) {
        return memberships.stream()
                .anyMatch(m -> m.getOrganization().equals(organization));
    }

    public boolean hasOrganizationRole(String organizationSlug, String roleSlug) {
        return memberships.stream()
                .filter(m -> m.getOrganization().getSlug().equals(organizationSlug))
                .allMatch(m -> m.hasRole(roleSlug));
    }

    public boolean hasOrganizationRole(Organization organization, String roleSlug) {
        return memberships.stream()
                .filter(m -> m.getOrganization().equals(organization))
                .allMatch(m -> m.hasRole(roleSlug));
    }

    public boolean isOrganizationStaff(String organizationSlug) {
        return memberships.stream()
                .filter(m -> m.getOrganization().getSlug().equals(organizationSlug))
                .allMatch(OrganizationMember::isStaff);
    }

    public boolean isOrganizationStaff(Organization organization) {
        return memberships.stream()
                .filter(m -> m.getOrganization().equals(organization))
                .allMatch(OrganizationMember::isStaff);
    }

    public boolean isOrganizationOwner(String organizationSlug) {
        return memberships.stream()
                .filter(m -> m.getOrganization().getSlug().equals(organizationSlug))
                .allMatch(OrganizationMember::isOwner);
    }

    public boolean isOrganizationOwner(Organization organization) {
        return memberships.stream()
                .filter(m -> m.getOrganization().equals(organization))
                .allMatch(OrganizationMember::isOwner);
    }
}
