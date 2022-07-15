package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ExpiredTokenException;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/organizations/{slug}/manage")
public class OrganizationManagementController {
    private final OrganizationService organizationService;

    public OrganizationManagementController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping
    public String getDetailsPanel(@PathVariable String slug, Model model) throws Exception {
        var organization = organizationService.getBySlug(slug);
        model.addAttribute("organization", organization);
        return "organizations/manage";
    }

   @GetMapping("/members")
   public String getMemberPanel(@PathVariable String slug,
                                @PageableDefault Pageable pageable,
                                Model model) throws Exception {
       var organization = organizationService.getBySlug(slug);
       var members = organizationService.findAllMembersByOrganization(organization, pageable);

       model.addAttribute("organization", organization);
       model.addAttribute("members", members);
       return "organizations/manage_members";
   }

   @GetMapping("/roles")
   public String getRolesPanel(@PathVariable String slug, Model model) throws Exception {
       var organization = organizationService.getBySlug(slug);
       model.addAttribute("organization", organization);
       return "organizations/manage_roles";
   }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ExpiredTokenException.class)
    public String organizationNotFoundHandler() {
        return "redirect:/organizations";
    }
}
