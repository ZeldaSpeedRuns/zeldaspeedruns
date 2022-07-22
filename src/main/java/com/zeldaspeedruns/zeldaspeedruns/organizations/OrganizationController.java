package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.organizations.forms.CreateOrganizationForm;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ExpiredTokenException;
import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetails;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @ModelAttribute("organization")
    public Organization loadOrganization(@PathVariable(value = "slug", required = false) String slug)  {
        return organizationService.getOrganizationBySlug(slug).orElse(null);
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
                                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "organizations/create_form";
        }

        var organization = organizationService.createOrganization(form.getName(), form.getSlug());
        return "redirect:/organizations/" + organization.getSlug();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ExpiredTokenException.class)
    public String organizationNotFoundHandler() {
        return "redirect:/organizations";
    }
}
