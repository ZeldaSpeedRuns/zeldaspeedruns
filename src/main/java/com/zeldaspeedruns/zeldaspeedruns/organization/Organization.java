package com.zeldaspeedruns.zeldaspeedruns.organization;

import com.zeldaspeedruns.zeldaspeedruns.core.AbstractUuidEntity;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "organizations")
@ToString(onlyExplicitlyIncluded = true)
public class Organization extends AbstractUuidEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    @ToString.Include
    private Long id;

    @Column(name = "name", nullable = false)
    @ToString.Include
    private String name;

    @Column(name = "slug", unique = true, nullable = false, updatable = false)
    @ToString.Include
    private String slug;

    @Column(name = "description")
    private String description;

    protected Organization() {
    }

    public Organization(String name, String slug) {
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.slug = Objects.requireNonNull(slug, "slug must not be null");
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public String getSlug() {
        return slug;
    }

    public Optional<String> getDescription() {
        return Optional.ofNullable(description);
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }
}
