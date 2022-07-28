package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class OrganizationPermissionEvaluator implements PermissionEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(OrganizationPermissionEvaluator.class);

    private final OrganizationService organizationService;

    public OrganizationPermissionEvaluator(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object domainObject, Object permission) {
        if (!(permission instanceof String role)) {
            return false;
        }

        if (authentication == null || !(authentication.getPrincipal() instanceof ZsrUserDetails userDetails)) {
            return false;
        }

        if (!(domainObject instanceof Organization organization)) {
            return false;
        }

        var user = userDetails.getUser();
        var membershipOptional = organizationService.findMembership(organization, user);

        if (membershipOptional.isPresent()) {
            var membership = membershipOptional.get();
            return membership.hasRole(role);
        } else {
            return false;
        }
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        throw new RuntimeException("not implemented");
    }
}
