package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "organization_members", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization_id", "user_id"})
})
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = "OrganizationMember.user",
                attributeNodes = @NamedAttributeNode("user")
        ),
        @NamedEntityGraph(
                name = "OrganizationMember.organization",
                attributeNodes = @NamedAttributeNode("organization")
        ),
        @NamedEntityGraph(
                name = "OrganizationMember.roles",
                attributeNodes = @NamedAttributeNode("roles")
        ),
        @NamedEntityGraph(
                name = "OrganizationMember.organizationAndRoles",
                attributeNodes = {
                        @NamedAttributeNode("organization"),
                        @NamedAttributeNode("roles")
                }
        ),
        @NamedEntityGraph(
                name = "OrganizationMember.all",
                attributeNodes = {
                        @NamedAttributeNode("user"),
                        @NamedAttributeNode("organization"),
                        @NamedAttributeNode("roles")
                }
        )
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

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "organization_member_roles",
            joinColumns = @JoinColumn(name = "member_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<OrganizationRole> roles = new HashSet<>();

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

    public Set<OrganizationRole> getRoles() {
        return roles;
    }

    public boolean isOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public boolean isStaff() {
        return staff || owner;
    }

    public void setStaff(boolean staff) {
        this.staff = staff;
    }

    public boolean hasRole(OrganizationRole role) {
        return roles.contains(role);
    }

    public boolean hasRole(String slug) {
        return roles.stream()
                .anyMatch(r -> slug.equals(r.getSlug()));
    }
}
