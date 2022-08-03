package com.zeldaspeedruns.zeldaspeedruns.organizations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationInviteUseRepository extends JpaRepository<OrganizationInviteUse, Long> {
    long countByInvite(OrganizationInvite invite);

    Page<OrganizationInviteUse> findByInvite(OrganizationInvite invite, Pageable pageable);
}
