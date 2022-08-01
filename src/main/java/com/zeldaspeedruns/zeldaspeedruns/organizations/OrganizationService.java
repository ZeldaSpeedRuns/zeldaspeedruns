package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.InvalidSlugException;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * The organization service specifies a means of managing organizations.
 */
public interface OrganizationService {
    /**
     * Creates a new Organization.
     *
     * @param name The desired name
     * @param slug A valid slug
     * @return The new organization
     * @throws InvalidSlugException           If the slug is invalid
     * @throws OrganizationSlugInUseException If the slug is in use already
     */
    Organization createOrganization(String name, String slug);

    /**
     * Adds a new member to an organization.
     *
     * @param organization The organization to add the member to
     * @param user         The user to create a membership for
     * @return The organization membership
     * @throws MembershipExistsException If the user is a member of the organization
     */
    OrganizationMember addOrganizationMember(Organization organization, ZsrUser user);

    /**
     * Deletes an organization.
     *
     * @param organization The organization to delete
     */
    void deleteOrganization(Organization organization);

    /**
     * Retrieves an organization.
     *
     * @param slug The slug of the organization to be found
     * @return An optional containing an Organization if found
     */
    Optional<Organization> findOrganizationBySlug(String slug);

    /**
     * Retrieves all organizations.
     *
     * @param pageable Pageable specifying limit and sort of the results.
     * @return Page containing 0 or more organizations.
     */
    Page<Organization> findAllOrganizations(Pageable pageable);

    /**
     * Retrieves all members of an organization.
     *
     * @param organization The organization to find the members of.
     * @param pageable     Pageable specifying limit and sort of the results.
     * @return Page containing 0 or more organization members.
     */
    Page<OrganizationMember> findAllMembersByOrganization(Organization organization, Pageable pageable);

    /**
     * Retrieves all organization memberships for a user.
     *
     * @param user The user to find memberships for.
     * @return Iterable containing 0 or more memberships.
     */
    Iterable<OrganizationMember> findAllMembershipsByUser(ZsrUser user);

    /**
     * Retrieves a membership for a specific organization and user.
     *
     * @param organization The organization to find the membership for.
     * @param user         The user to find the membership for.
     * @return Optional containing the organization membership if found.
     */
    Optional<OrganizationMember> findMembership(Organization organization, ZsrUser user);

    /**
     * Creates an invitation token that can be used to join an Organization.
     *
     * @param organization The organization to create the invite token for.
     * @param user         The user that created the token in the audit log.
     * @return Invitation token entity.
     */
    OrganizationInvite createInvite(Organization organization, ZsrUser user);

    /**
     * Deletes an invitation token, making it invalid to use.
     *
     * @param invite The invitation to delete.
     */
    void deleteInvite(OrganizationInvite invite);

    /**
     * Joins an organization, making the user a member of the organization.
     *
     * @param invite The invite token to be used.
     * @param user   The user to make a member.
     * @return The new membership entity.
     * @throws MembershipExistsException If the user is a member already.
     * @throws InvalidInviteException    If the invite has expired, been invalidated, or reached its usage limit.
     */
    OrganizationMember joinOrganization(OrganizationInvite invite, ZsrUser user);
}
