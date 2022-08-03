package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.organizations.projections.InviteWithUsageCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationInviteRepository extends JpaRepository<OrganizationInvite, Long> {
    Optional<OrganizationInvite> findByUuid(UUID uuid);

    Page<OrganizationInvite> findByOrganization(Organization organization, Pageable pageable);

    @Query(value = """
            SELECT new com.zeldaspeedruns.zeldaspeedruns.organizations.projections.InviteWithUsageCount(
                i.id,
                i.uuid,
                i.expiresAt,
                i.maxUses,
                COUNT(iu.id),
                i.invalidated
            )
            FROM OrganizationInvite i
                    LEFT JOIN OrganizationInviteUse iu ON iu.invite = i
            WHERE i.organization = :organization
            GROUP BY i.id
            """)
    Page<InviteWithUsageCount> findByOrganizationWithUsage(Organization organization, Pageable pageable);
}
