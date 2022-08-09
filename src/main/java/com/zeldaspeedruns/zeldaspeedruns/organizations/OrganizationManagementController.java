package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.organizations.forms.CreateInviteForm;
import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

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

    @GetMapping("/invites/create")
    public String getCreateInviteForm(@ModelAttribute("form") CreateInviteForm form) {
        return "organizations/create_invite_form";
    }

    @PostMapping("/invites/create")
    @Transactional
    public String postCreateInviteForm(Organization organization,
                                       @AuthenticationPrincipal ZsrUserDetails principal,
                                       @ModelAttribute("form") @Valid CreateInviteForm form,
                                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "organizations/create_invite_form";
        }

        var invite = organizationService.createInvite(organization, principal.getUser());

        if (form.getExpiresAt() != null) {
            invite.setExpiresAt(OffsetDateTime.of(form.getExpiresAt(), ZoneOffset.UTC));
        }
        if (form.getMaxUses() != null && form.getMaxUses() > 0) {
            invite.setMaxUses(form.getMaxUses());
        }

        return "redirect:/organizations/" + organization.getSlug() + "/manage/invites";
    }
}
