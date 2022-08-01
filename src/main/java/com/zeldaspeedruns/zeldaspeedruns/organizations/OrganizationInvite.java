package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.lang.Nullable;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "organization_invites")
public class OrganizationInvite {
    @NaturalId
    @Column(name = "uuid", unique = true, updatable = false, nullable = false)
    private final UUID uuid = UUID.randomUUID();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "organization_id", referencedColumnName = "id", nullable = false, updatable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
    private ZsrUser user;

    @Column(name = "max_uses")
    private Long maxUses;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "invalidated", nullable = false)
    private Boolean invalidated = false;

    protected OrganizationInvite() {
    }

    public OrganizationInvite(Organization organization, ZsrUser user) {
        this.organization = organization;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public UUID getUUID() {
        return uuid;
    }

    public Organization getOrganization() {
        return organization;
    }

    public ZsrUser getUser() {
        return user;
    }

    public Optional<Long> getMaxUses() {
        return Optional.ofNullable(maxUses);
    }

    public void setMaxUses(@Nullable Long maxUses) {
        this.maxUses = maxUses;
    }

    public Optional<OffsetDateTime> getExpiresAt() {
        return Optional.ofNullable(expiresAt);
    }

    public void setExpiresAt(@Nullable OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean hasExpired(OffsetDateTime now) {
        return expiresAt != null && expiresAt.isBefore(now);
    }

    public boolean isInvalidated() {
        return invalidated;
    }

    public void setInvalidated(boolean invalidated) {
        this.invalidated = invalidated;
    }
}
