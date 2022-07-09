package com.zeldaspeedruns.zeldaspeedruns.organization;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationServiceImpl implements OrganizationService {
    private final static Logger logger = LoggerFactory.getLogger(OrganizationServiceImpl.class);

    private final OrganizationRepository organizationRepository;
    private final MutableAclService aclService;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository,
                                   MutableAclService aclService) {
        this.organizationRepository = organizationRepository;
        this.aclService = aclService;
    }

    private MutableAcl getOrganizationAcl(Organization organization) {
        ObjectIdentity identity = new ObjectIdentityImpl(Organization.class, organization.getId());

        try {
            return (MutableAcl) aclService.readAclById(identity);
        } catch (NotFoundException ignored) {
            return aclService.createAcl(identity);
        }
    }

    @Override
    public Page<Organization> findAll(Pageable pageable) {
        return organizationRepository.findAll(pageable);
    }

    @Override
    public Organization getBySlug(String slug) throws OrganizationNotFoundException {
        return organizationRepository.findBySlug(slug)
                .orElseThrow(() -> new OrganizationNotFoundException(String.format("no organization with slug '%s' found", slug)));
    }

    @Override
    public Organization createOrganization(String name, String slug) {
        return organizationRepository.save(new Organization(name, slug));
    }

    @Override
    @Transactional
    public Organization createOrganization(ZsrUser user, String name, String slug) {
        var organization = createOrganization(name, slug);
        grantOrganizationPermission(organization, user, BasePermission.ADMINISTRATION);
        return organization;
    }

    @Override
    public Organization updateOrganization(Organization organization) {
        return organizationRepository.save(organization);
    }

    @Override
    @Transactional
    public void deleteOrganization(Organization organization) {
        ObjectIdentity identity = new ObjectIdentityImpl(Organization.class, organization.getId());
        aclService.deleteAcl(identity, true);
        organizationRepository.delete(organization);
    }

    @Override
    @Transactional
    public void grantOrganizationPermission(Organization organization, ZsrUser user, Permission permission) {
        Sid sid = new PrincipalSid(user.getUsername());
        MutableAcl acl = getOrganizationAcl(organization);
        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        aclService.updateAcl(acl);
        logger.debug("Granted permission {} to {} for {}", permission, user, organization);
    }
}
