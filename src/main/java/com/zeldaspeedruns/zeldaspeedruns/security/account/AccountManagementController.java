package com.zeldaspeedruns.zeldaspeedruns.security.account;

import com.zeldaspeedruns.zeldaspeedruns.organizations.OrganizationService;
import com.zeldaspeedruns.zeldaspeedruns.security.oauth2.OAuth2AccountLinkService;
import com.zeldaspeedruns.zeldaspeedruns.security.oauth2.OAuth2RegisteredClientService;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetails;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/account")
public class AccountManagementController {
    private final OAuth2AccountLinkService accountLinkService;
    private final OrganizationService organizationService;
    private final OAuth2RegisteredClientService registeredClientService;

    public AccountManagementController(OAuth2AccountLinkService accountLinkService,
                                       OrganizationService organizationService,
                                       OAuth2RegisteredClientService registeredClientService) {
        this.accountLinkService = accountLinkService;
        this.organizationService = organizationService;
        this.registeredClientService = registeredClientService;
    }

    @ModelAttribute("user")
    public ZsrUser userAttribute(@AuthenticationPrincipal ZsrUserDetails principal) {
        return principal.getUser();
    }

    @GetMapping
    public String getAccountDetails(@AuthenticationPrincipal ZsrUserDetails principal,
                                    Model model) {
        model.addAttribute("accountLinks", accountLinkService.getAccountLinksForUser(principal.getUser()));
        return "ucp/account_details";
    }

    @GetMapping("/organizations")
    public String getOrganizationMembershipsPanel(@AuthenticationPrincipal ZsrUserDetails principal,
                                                  Model model) {
        model.addAttribute("memberships", organizationService.findAllMembershipsByUser(principal.getUser()));
        return "ucp/account_organizations";
    }

    @GetMapping("/api-clients")
    public String getApiClients(@AuthenticationPrincipal ZsrUserDetails principal,
                                Model model) {
        model.addAttribute("user", principal.getUser());
        model.addAttribute("apiClients", registeredClientService.getRegisteredClientsByUser(principal.getUser()));
        return "ucp/account_api_clients";
    }

    @GetMapping("/api-clients/create")
    public String getCreateApiClientForm(@ModelAttribute("form") CreateApiClientForm form,
                                         Model model) {
        return "ucp/create_api_client_form";
    }

    @PostMapping("/api-clients/create")
    public String postCreateApiClientForm(@AuthenticationPrincipal ZsrUserDetails principal,
                                          @ModelAttribute("form") @Valid CreateApiClientForm form,
                                          BindingResult bindingResult,
                                          Model model) {
        if (bindingResult.hasErrors()) {
            return "ucp/create_api_client_form";
        }

        var redirectUrls = form.getRedirectUrls().lines()
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());

        var client = registeredClientService.createClientForUser(principal.getUser(), form.getName(), redirectUrls);
        return "redirect:/user/account/api-clients/" + client.getUuid();
    }
}
