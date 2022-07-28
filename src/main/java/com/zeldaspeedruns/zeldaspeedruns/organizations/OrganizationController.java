package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.organizations.forms.CreateOrganizationForm;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ExpiredTokenException;
import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetails;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @ModelAttribute("organization")
    public Organization loadOrganization(@PathVariable(value = "slug", required = false) String slug) {
        if (slug != null && !slug.isEmpty()) {
            var organizationOptional = organizationService.findOrganizationBySlug(slug);
            if (organizationOptional.isPresent()) {
                return organizationOptional.get();
            } else {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "organization not found");
            }
        } else {
            return null;
        }
    }

    @GetMapping
    public String getOrganizations(@PageableDefault(sort = "name") Pageable pageable,
                                   Model model) {
        var organizations = this.organizationService.findAllOrganizations(pageable);
        model.addAttribute("organizations", organizations);
        return "organizations/list";
    }

    @GetMapping("/{slug}")
    public String getOrganization(Organization organization) {
        return "organizations/detail";
    }

    @GetMapping("/create")
    public String getOrganizationCreationForm(@ModelAttribute("form") CreateOrganizationForm form) {
        return "organizations/create_form";
    }

    @PostMapping("/create")
    public String postOrganizationCreationForm(@AuthenticationPrincipal ZsrUserDetails principal,
                                               @ModelAttribute("form") @Valid CreateOrganizationForm form,
                                               BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            return "organizations/create_form";
        }

        var organization = organizationService.createOrganization(form.getName(), form.getSlug());
        return "redirect:/organizations/" + organization.getSlug();
    }
}
