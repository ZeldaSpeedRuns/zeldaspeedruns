package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import jakarta.persistence.Entity;
import org.hibernate.collection.internal.PersistentSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface OrganizationMemberRepository extends JpaRepository<OrganizationMember, Long> {
    @EntityGraph("OrganizationMember.user")
    Page<OrganizationMember> findAllByOrganization(Organization organization, Pageable pageable);

    @EntityGraph("OrganizationMember.organizationAndRoles")
    Iterable<OrganizationMember> findAllByUser(ZsrUser user);

    boolean existsByOrganizationAndUser(Organization organization, ZsrUser user);
}
