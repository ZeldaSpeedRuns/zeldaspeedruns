package com.zeldaspeedruns.zeldaspeedruns.organization;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.acls.model.Permission;

public interface OrganizationService {
    Page<Organization> findAll(Pageable pageable);

    Organization getBySlug(String slug) throws OrganizationNotFoundException;

    @PreAuthorize("hasRole('ADMIN')")
    Organization createOrganization(String name, String slug);

    @PreAuthorize("hasRole('ADMIN')")
    Organization createOrganization(ZsrUser user, String name, String slug);

    @PreAuthorize("hasRole('ADMIN') or hasPermission(#organization, 'WRITE')")
    Organization updateOrganization(Organization organization);

    @PreAuthorize("hasRole('ADMIN') or hasPermission(#organization, 'DELETE')")
    void deleteOrganization(Organization organization);

    @PreAuthorize("hasRole('ADMIN') or hasPermission(#organization, 'ADMINISTRATION')")
    void grantOrganizationPermission(Organization organization, ZsrUser user, Permission permission);
}
