package com.zeldaspeedruns.zeldaspeedruns.organizations;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "organization_roles", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization_id", "slug"})
})
public class OrganizationRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false, updatable = false)
    private Organization organization;

    @Column(name = "name", nullable = false)
    private String name;

    @NaturalId
    @Column(name = "slug", updatable = false, nullable = false)
    private String slug;

    protected OrganizationRole() {
    }

    public OrganizationRole(Organization organization, String name, String slug) {
        this.organization = Objects.requireNonNull(organization);
        this.name = Objects.requireNonNull(name);
        this.slug = Objects.requireNonNull(slug);
    }

    public Long getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name);
    }

    public String getSlug() {
        return slug;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrganizationRole that)) return false;
        return organization.equals(that.organization) && slug.equals(that.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(organization, slug);
    }
}
