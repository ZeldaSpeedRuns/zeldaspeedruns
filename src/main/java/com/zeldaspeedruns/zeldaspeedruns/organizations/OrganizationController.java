package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ExpiredTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/organizations")
public class OrganizationController {
    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @GetMapping("/{slug}")
    public String getOrganization(@PathVariable String slug, Model model) throws Exception {
        var organization = organizationService.getBySlug(slug);
        model.addAttribute("organization", organization);
        return "organizations/detail";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ExpiredTokenException.class)
    public String organizationNotFoundHandler() {
        return "redirect:/organizations";
    }
}
