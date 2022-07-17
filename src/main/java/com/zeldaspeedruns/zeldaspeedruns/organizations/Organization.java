package com.zeldaspeedruns.zeldaspeedruns.organizations;

import org.hibernate.annotations.NaturalId;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "organizations")
public class Organization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, updatable = false, unique = true)
    private final UUID uuid = UUID.randomUUID();

    @Column(name = "name", nullable = false)
    private String name;

    @NaturalId
    @Column(name = "slug", nullable = false, updatable = false, unique = true)
    private String slug;

    @Column(name = "icon")
    private String icon;

    @OneToMany(mappedBy = "organization", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<OrganizationRole> roles = new HashSet<>();

    protected Organization() {
    }

    public Organization(String name, String slug) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.slug = Objects.requireNonNull(slug, "slug must not be null");
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Set<OrganizationRole> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Organization that)) return false;
        return uuid.equals(that.uuid) && slug.equals(that.slug);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, slug);
    }
}
