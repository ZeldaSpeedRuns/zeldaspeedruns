package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;

import javax.persistence.*;

@Entity
@Table(name = "organization_members", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization_id", "user_id"})
})
public class OrganizationMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false, updatable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private ZsrUser user;

    @Column(name = "is_owner")
    private boolean owner = false;

    @Column(name = "is_staff", nullable = false)
    private boolean staff = false;

    protected OrganizationMember() {
    }

    public OrganizationMember(Organization organization, ZsrUser user) {
        this.organization = organization;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public Organization getOrganization() {
        return organization;
    }

    public ZsrUser getUser() {
        return user;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean isStaff() {
        return staff;
    }

    public void setStaff(boolean staff) {
        this.staff = staff;
    }
}
