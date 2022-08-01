package com.zeldaspeedruns.zeldaspeedruns.organizations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationInviteUseRepository extends JpaRepository<OrganizationInviteUse, Long> {
    long countByInvite(OrganizationInvite invite);
}
