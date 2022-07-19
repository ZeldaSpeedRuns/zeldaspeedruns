package com.zeldaspeedruns.zeldaspeedruns.security.account;

import com.zeldaspeedruns.zeldaspeedruns.organizations.OrganizationService;
import com.zeldaspeedruns.zeldaspeedruns.security.oauth2.OAuth2AccountLinkService;
import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user/account")
public class AccountManagementController {
    private final OAuth2AccountLinkService accountLinkService;
    private final OrganizationService organizationService;

    public AccountManagementController(OAuth2AccountLinkService accountLinkService,
                                       OrganizationService organizationService) {
        this.accountLinkService = accountLinkService;
        this.organizationService = organizationService;
    }

    @GetMapping
    public String getAccountDetails(@AuthenticationPrincipal ZsrUserDetails principal,
                                    Model model) {
        model.addAttribute("user", principal.getUser());
        model.addAttribute("accountLinks", accountLinkService.getAccountLinksForUser(principal.getUser()));
        return "ucp/account_details";
    }

    @GetMapping("/organizations")
    public String getOrganizationMembershipsPanel(@AuthenticationPrincipal ZsrUserDetails principal,
                                                  Model model) {
        var user = principal.getUser();
        model.addAttribute("user", user);
        model.addAttribute("memberships", organizationService.findAllMembershipsByUser(user));
        return "ucp/account_organizations";
    }
}
