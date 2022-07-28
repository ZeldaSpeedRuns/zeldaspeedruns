package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Long> {
    @EntityGraph("OrganizationMember.user")
    Page<OrganizationMember> findAllByOrganization(Organization organization, Pageable pageable);

    @EntityGraph("OrganizationMember.organizationAndRoles")
    Iterable<OrganizationMember> findAllByUser(ZsrUser user);

    @EntityGraph("OrganizationMember.roles")
    Optional<OrganizationMember> findByUserAndOrganization(ZsrUser user, Organization organization);

    boolean existsByOrganizationAndUser(Organization organization, ZsrUser user);
}
