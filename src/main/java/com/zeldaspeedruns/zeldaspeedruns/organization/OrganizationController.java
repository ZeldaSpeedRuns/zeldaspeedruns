package com.zeldaspeedruns.zeldaspeedruns.organization;

import com.zeldaspeedruns.zeldaspeedruns.organization.forms.CreateOrganizationForm;
import com.zeldaspeedruns.zeldaspeedruns.organization.forms.EditOrganizationForm;
import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetails;
import com.zeldaspeedruns.zeldaspeedruns.validation.constraints.Slug;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Configuration
@RequestMapping(OrganizationController.BASE_URL)
public class OrganizationController {
    public final static String BASE_URL = "/organizations";
    public final static String CREATE_ORGANIZATION_URL = "/create";
    public final static String GET_ORGANIZATION_URL = "/{slug}";
    public final static String EDIT_ORGANIZATION_URL = "/{slug}/edit";
    public final static String DELETE_ORGANIZATION_URL = "/{slug}/delete";

    public final static String ORGANIZATION_DETAIL_VIEW_NAME = "organization/organization_detail";
    public final static String ORGANIZATION_LIST_VIEW_NAME = "organization/organization_list";
    public final static String CREATE_ORGANIZATION_FORM_VIEW_NAME = "organization/create_organization_form";
    public final static String EDIT_ORGANIZATION_FORM_VIEW_NAME = "organization/edit_organization_form";
    public final static String DELETE_ORGANIZATION_VIEW_NAME = "organization/delete_organization";

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public String getOrganizations(@PageableDefault Pageable pageable,
                                   Model model) {
        model.addAttribute("organizations", organizationService.findAll(pageable));
        return "organization/organization_list";
    }

    @GetMapping(CREATE_ORGANIZATION_URL)
    @PreAuthorize("hasRole('ADMIN')")
    public String createOrganizationForm(@ModelAttribute("form") CreateOrganizationForm form) {
        return "organization/create_organization_form";
    }

    @PostMapping(CREATE_ORGANIZATION_URL)
    public String createOrganization(@ModelAttribute("form") @Valid CreateOrganizationForm form,
                                     BindingResult bindingResult,
                                     @AuthenticationPrincipal ZsrUserDetails principal) {
        if (bindingResult.hasErrors()) {
            return CREATE_ORGANIZATION_FORM_VIEW_NAME;
        }

        var user = principal.getUser();
        var name = form.getName();
        var slug = form.getSlug();

        Organization organization = organizationService.createOrganization(user, name, slug);
        return "redirect:/organizations/" + organization.getSlug();
    }

    @GetMapping(GET_ORGANIZATION_URL)
    public String getOrganization(@PathVariable @Valid @Slug String slug,
                                  Model model) throws OrganizationNotFoundException {
        model.addAttribute("organization", organizationService.getBySlug(slug));
        return "organization/organization_detail";
    }

    @GetMapping(EDIT_ORGANIZATION_URL)
    public String editOrganizationForm(@PathVariable @Valid @Slug String slug,
                                       @ModelAttribute("form") EditOrganizationForm form) throws OrganizationNotFoundException {
        Organization organization = organizationService.getBySlug(slug);
        form.setName(organization.getName());
        form.setDescription(organization.getDescription().orElse(null));
        return EDIT_ORGANIZATION_FORM_VIEW_NAME;
    }

    @PostMapping(EDIT_ORGANIZATION_URL)
    public String editOrganization(@PathVariable @Valid @Slug String slug,
                                   @ModelAttribute("form") @Valid EditOrganizationForm form,
                                   BindingResult bindingResult) throws OrganizationNotFoundException {
        if (bindingResult.hasErrors()) {
            return EDIT_ORGANIZATION_FORM_VIEW_NAME;
        }

        Organization organization = organizationService.getBySlug(slug);
        organization.setName(form.getName());
        organization.setDescription(form.getDescription());
        organizationService.updateOrganization(organization);
        return "redirect:/organizations/" + organization.getSlug();
    }

    @PostMapping(DELETE_ORGANIZATION_URL)
    public String deleteOrganization(@PathVariable @Valid @Slug String slug) throws OrganizationNotFoundException {
        var organization = organizationService.getBySlug(slug);
        organizationService.deleteOrganization(organization);
        return "redirect:/organizations";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(OrganizationNotFoundException.class)
    public void organizationNotFound() {
        // intentionally left blank
    }
}
