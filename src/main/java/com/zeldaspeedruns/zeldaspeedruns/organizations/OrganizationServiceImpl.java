package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository memberRepository;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository,
                                   OrganizationMemberRepository memberRepository) {
        this.organizationRepository = organizationRepository;
        this.memberRepository = memberRepository;
    }

    @Override
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
    public OrganizationMember addOrganizationMember(Organization organization, ZsrUser user) {
        if (memberRepository.existsByOrganizationAndUser(organization, user)) {
            throw new MembershipExistsException("already a member of organization");
        }

        var membership = new OrganizationMember(organization, user);
        return memberRepository.save(membership);
    }

    @Override
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
}
