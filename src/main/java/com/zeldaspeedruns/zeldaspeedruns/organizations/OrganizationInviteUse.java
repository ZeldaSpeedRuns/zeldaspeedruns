package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "organization_invite_uses")
public class OrganizationInviteUse {
    @Column(name = "used_at", updatable = false, nullable = false)
    private final OffsetDateTime usedAt = OffsetDateTime.now();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "invite_id", referencedColumnName = "id", nullable = false, updatable = false)
    private OrganizationInvite invite;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
    private ZsrUser user;

    protected OrganizationInviteUse() {
    }

    public OrganizationInviteUse(OrganizationInvite invite, ZsrUser user) {
        this.invite = Objects.requireNonNull(invite);
        this.user = Objects.requireNonNull(user);
    }

    public Long getId() {
        return id;
    }

    public OrganizationInvite getInvite() {
        return invite;
    }

    public ZsrUser getUser() {
        return user;
    }

    public OffsetDateTime getUsedAt() {
        return usedAt;
    }
}
