package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;

public interface OrganizationService {
    Organization createOrganization(String name, String slug);

    OrganizationMember addOrganizationMember(Organization organization, ZsrUser user);

    void deleteOrganization(Organization organization);

    Optional<Organization> getOrganizationBySlug(String slug);

    Page<Organization> findAllOrganizations(Pageable pageable);

    Page<OrganizationMember> findAllMembersByOrganization(Organization organization, Pageable pageable);

    Iterable<OrganizationMember> findAllMembershipsByUser(ZsrUser user);
}
