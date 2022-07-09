package com.zeldaspeedruns.zeldaspeedruns.organization;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface OrganizationRepository extends PagingAndSortingRepository<Organization, Long> {
    Optional<Organization> findBySlug(String slug);
}
