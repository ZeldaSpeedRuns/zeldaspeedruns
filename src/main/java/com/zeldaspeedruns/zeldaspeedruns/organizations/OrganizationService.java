package com.zeldaspeedruns.zeldaspeedruns.organizations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrganizationService {
    Organization getBySlug(String slug) throws OrganizationNotFoundException;

    Page<Organization> findAll(Pageable pageable);

    Page<OrganizationMember> findAllMembersByOrganization(Organization organization, Pageable pageable);
}
