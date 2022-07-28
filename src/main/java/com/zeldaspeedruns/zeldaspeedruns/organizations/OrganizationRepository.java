package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.organizations.projections.OrganizationWithMemberCount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    @EntityGraph("Organization.roles")
    Optional<Organization> findBySlug(String slug);

    @Query(
            value = """
                    SELECT o.id AS id, o.name AS name, o.slug AS slug, o.icon AS icon, COUNT(m) AS memberCount
                    FROM Organization o JOIN o.members m
                    GROUP BY o.id
                    """,
            countQuery = "SELECT o FROM Organization o"
    )
    Page<OrganizationWithMemberCount> findAllWithMemberCount(Pageable pageable);
}
