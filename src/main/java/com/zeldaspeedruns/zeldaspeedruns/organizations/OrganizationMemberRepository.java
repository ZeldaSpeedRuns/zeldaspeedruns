package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import jakarta.persistence.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationMemberRepository extends PagingAndSortingRepository<OrganizationMember, Long> {
    @EntityGraph("OrganizationMember.user")
    Page<OrganizationMember> findAllByOrganization(Organization organization, Pageable pageable);

    @EntityGraph("OrganizationMember.organization")
    Iterable<OrganizationMember> findAllByUser(ZsrUser user);
}
