package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
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
    Page<OrganizationMember> findAllByOrganization(Organization organization, Pageable pageable);

    @Query("FROM OrganizationMember m JOIN FETCH m.organization WHERE m.user = :user")
    Iterable<OrganizationMember> findAllByUser(ZsrUser user);
}
