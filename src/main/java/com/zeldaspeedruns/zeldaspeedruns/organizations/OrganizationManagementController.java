package com.zeldaspeedruns.zeldaspeedruns.organizations;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/organizations/{slug}/manage")
@PreAuthorize("isAuthenticated()")
public class OrganizationManagementController {
    private final OrganizationService organizationService;

    public OrganizationManagementController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @ModelAttribute("organization")
    public Organization loadOrganization(@PathVariable String slug) {
        var organization = organizationService.findOrganizationBySlug(slug);

        if (organization.isPresent()) {
            return organization.get();
        } else {
            throw new RuntimeException("organization not found");
        }
    }

    @GetMapping
    public String getDetailsPanel(Organization organization) throws Exception {
        return "organizations/manage";
    }

    @GetMapping("/members")
    public String getMemberPanel(Organization organization,
                                 @PageableDefault Pageable pageable,
                                 Model model) {
        var members = organizationService.findAllMembersByOrganization(organization, pageable);
        model.addAttribute("members", members);

        return "organizations/manage_members";
    }

    @GetMapping("/roles")
    public String getRolesPanel(Organization organization) {
        return "organizations/manage_roles";
    }

    @GetMapping("/invites")
    public String getInvitesPanel(Organization organization,
                                  @PageableDefault Pageable pageable,
                                  Model model) throws Exception {
        var invites = organizationService.findAllInvitesByOrganization(organization, pageable);
        model.addAttribute("invites", invites);
        return "organizations/manage_invites";
    }
}
