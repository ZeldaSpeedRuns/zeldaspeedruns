package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetails;
import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetailsService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class ZsrOidcUserService extends OidcUserService {
    private final OAuth2AccountLinkService accountLinkService;

    public ZsrOidcUserService(OAuth2AccountLinkService accountLinkService) {
        this.accountLinkService = accountLinkService;
    }

    @Override
    public ZsrOidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        var oidcUser = super.loadUser(userRequest);
        var linkedAccount = accountLinkService.processUser(oidcUser, userRequest.getClientRegistration());
        return new ZsrOidcUser(new ZsrUserDetails(linkedAccount.getUser()), oidcUser);
    }
}
