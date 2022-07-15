package com.zeldaspeedruns.zeldaspeedruns.organizations;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationMemberRepository extends PagingAndSortingRepository<OrganizationMember, Long> {
    @Query(
            value = """
                    FROM OrganizationMember m
                    JOIN FETCH m.user u
                    WHERE m.organization = :organization""",
            countQuery = """
                    SELECT count(m) FROM OrganizationMember m
                    JOIN m.user 
                    WHERE m.organization = :organization
                    """
    )
    Page<OrganizationMember> findAllByOrganization(Pageable pageable, Organization organization);
}
