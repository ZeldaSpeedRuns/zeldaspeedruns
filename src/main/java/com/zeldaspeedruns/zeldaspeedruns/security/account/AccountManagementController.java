package com.zeldaspeedruns.zeldaspeedruns.security.account;

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

    public AccountManagementController(OAuth2AccountLinkService accountLinkService) {
        this.accountLinkService = accountLinkService;
    }

    @GetMapping
    public String getAccountDetails(@AuthenticationPrincipal ZsrUserDetails principal,
                                    Model model) {
        model.addAttribute("user", principal.getUser());
        model.addAttribute("accountLinks", accountLinkService.getAccountLinksForUser(principal.getUser()));
        return "ucp/account_details";
    }
}
