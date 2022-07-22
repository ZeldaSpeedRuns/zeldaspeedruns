package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

public interface OrganizationService {
    @PreAuthorize("hasRole('ADMIN')")
    Organization createOrganization(String name, String slug);

    OrganizationMember addOrganizationMember(Organization organization, ZsrUser user);

    @PreAuthorize("hasRole('ADMIN')")
    void deleteOrganization(Organization organization);

    Organization getOrganizationBySlug(String slug) throws OrganizationNotFoundException;

    Page<Organization> findAllOrganizations(Pageable pageable);

    Page<OrganizationMember> findAllMembersByOrganization(Organization organization, Pageable pageable);

    Iterable<OrganizationMember> findAllMembershipsByUser(ZsrUser user);
}
