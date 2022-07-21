package com.zeldaspeedruns.zeldaspeedruns.organizations;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends PagingAndSortingRepository<Organization, Long> {
    @EntityGraph("Organization.roles")
    Optional<Organization> findBySlug(String slug);
}
