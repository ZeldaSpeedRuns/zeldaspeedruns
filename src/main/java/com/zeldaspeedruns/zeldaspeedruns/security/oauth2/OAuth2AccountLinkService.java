package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.List;

public interface OAuth2AccountLinkService {
    OAuth2AccountLink processUser(OidcUser user, ClientRegistration registration) throws OAuth2AuthenticationException;

    OAuth2AccountLink processUser(OAuth2User user, ClientRegistration registration) throws OAuth2AuthenticationException;

    List<OAuth2AccountLink> getAccountLinksForUser(ZsrUser user);
}
