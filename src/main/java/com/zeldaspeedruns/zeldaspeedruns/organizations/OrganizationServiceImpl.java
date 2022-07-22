package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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
            throw new RuntimeException("already a member of organization");
        }

        var membership = new OrganizationMember(organization, user);
        return memberRepository.save(membership);
    }

    @Override
    public void deleteOrganization(Organization organization) {
        organizationRepository.delete(organization);
    }

    @Override
    public Organization getOrganizationBySlug(String slug) throws OrganizationNotFoundException {
        return organizationRepository
                .findBySlug(slug)
                .orElseThrow(() -> new OrganizationNotFoundException("organization not found"));
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
}
