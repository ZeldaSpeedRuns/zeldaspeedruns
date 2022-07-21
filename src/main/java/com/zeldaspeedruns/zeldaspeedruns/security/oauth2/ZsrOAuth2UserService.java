package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetailsService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class ZsrOAuth2UserService extends DefaultOAuth2UserService {
    private final OAuth2AccountLinkService accountLinkService;
    private final ZsrUserDetailsService userDetailsService;

    public ZsrOAuth2UserService(OAuth2AccountLinkService accountLinkService,
                                ZsrUserDetailsService userDetailsService) {
        this.accountLinkService = accountLinkService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public ZsrOAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        var linkedAccount = accountLinkService.processUser(oAuth2User, userRequest.getClientRegistration());
        return new ZsrOAuth2User(userDetailsService.load(linkedAccount.getUser()), oAuth2User);
    }
}
