package com.zeldaspeedruns.zeldaspeedruns.organizations;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationInviteRepository extends JpaRepository<OrganizationInvite, Long> {
}
