package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrganizationService {
    Organization createOrganization(String name, String slug);

    Organization getOrganizationBySlug(String slug) throws OrganizationNotFoundException;

    Page<Organization> findAllOrganizations(Pageable pageable);

    Page<OrganizationMember> findAllMembersByOrganization(Organization organization, Pageable pageable);

    Iterable<OrganizationMember> findAllMembershipsByUser(ZsrUser user);
}
