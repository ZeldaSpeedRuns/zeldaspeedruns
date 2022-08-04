package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.organizations.projections.InviteWithUsageProjection;
import com.zeldaspeedruns.zeldaspeedruns.security.SecureTokenUtils;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository memberRepository;
    private final OrganizationInviteRepository inviteRepository;
    private final OrganizationInviteUseRepository inviteUseRepository;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository,
                                   OrganizationMemberRepository memberRepository,
                                   OrganizationInviteRepository inviteRepository,
                                   OrganizationInviteUseRepository inviteUseRepository) {
        this.organizationRepository = organizationRepository;
        this.memberRepository = memberRepository;
        this.inviteRepository = inviteRepository;
        this.inviteUseRepository = inviteUseRepository;
    }

    @Override
    @Transactional
    public Organization createOrganization(String name, String slug) {
        if (organizationRepository.existsBySlug(slug)) {
            throw new OrganizationSlugInUseException("organization with that slug already exists");
        }

        var organization = new Organization(name, slug);

        // Create default roles for the organization.
        var roles = organization.getRoles();
        roles.add(new OrganizationRole(organization, "Tracker", "tracker"));
        roles.add(new OrganizationRole(organization, "Commentator", "commentator"));
        roles.add(new OrganizationRole(organization, "Restreamer", "restreamer"));

        return organizationRepository.save(organization);
    }

    @Override
    @Transactional
    public OrganizationMember addOrganizationMember(Organization organization, ZsrUser user) {
        if (memberRepository.existsByOrganizationAndUser(organization, user)) {
            throw new MembershipExistsException("already a member of organization");
        }

        var membership = new OrganizationMember(organization, user);
        return memberRepository.save(membership);
    }

    @Override
    @Transactional
    public void deleteOrganization(Organization organization) {
        organizationRepository.delete(organization);
    }

    @Override
    public Optional<Organization> findOrganizationBySlug(String slug) {
        return organizationRepository.findBySlug(slug);
    }

    @Override
    public Page<Organization> findAllOrganizations(Pageable pageable) {
        return organizationRepository.findAll(pageable);
    }

    @Override
    public Page<OrganizationMember> findAllMembersByOrganization(Organization organization, Pageable pageable) {
        return memberRepository.findAllByOrganization(organization, pageable);
    }

    @Override
    public Iterable<OrganizationMember> findAllMembershipsByUser(ZsrUser user) {
        return memberRepository.findAllByUser(user);
    }

    @Override
    public Optional<OrganizationMember> findMembership(Organization organization, ZsrUser user) {
        return memberRepository.findByUserAndOrganization(user, organization);
    }

    @Override
    @Transactional
    public OrganizationInvite createInvite(Organization organization, ZsrUser user) {
        var code = SecureTokenUtils.generateAlphanumericToken(OrganizationInvite.INVITE_CODE_LENGTH);
        return inviteRepository.save(new OrganizationInvite(organization, user, code));
    }

    @Override
    @Transactional
    public void deleteInvite(OrganizationInvite invite) {
        invite.setInvalidated(true);
    }

    @Override
    @Transactional
    public OrganizationMember joinOrganization(OrganizationInvite invite, ZsrUser user) {
        if (invite.isInvalidated()) {
            throw new InvalidInviteException("this invite has been invalidated");
        }

        if (invite.hasExpired(OffsetDateTime.now())) {
            throw new InvalidInviteException("invite has expired");
        }

        if (invite.getMaxUses().isPresent()) {
            var maxUses = invite.getMaxUses().get();
            var uses = inviteUseRepository.countByInvite(invite);
            if (uses >= maxUses) {
                throw new InvalidInviteException("usage limit reached");
            }
        }

        var membership = addOrganizationMember(invite.getOrganization(), user);
        inviteUseRepository.save(new OrganizationInviteUse(invite, user));
        return membership;
    }

    @Override
    public Optional<OrganizationInvite> findInviteByCode(String code) {
        return inviteRepository.findByCode(code);
    }

    @Override
    public Page<InviteWithUsageProjection> findAllInvitesByOrganization(Organization organization, Pageable pageable) {
        return inviteRepository.findByOrganizationWithUsage(organization, pageable);
    }

    @Override
    public Page<OrganizationInviteUse> findAllInviteUsesByInvite(OrganizationInvite invite, Pageable pageable) {
        return inviteUseRepository.findByInvite(invite, pageable);
    }
}
