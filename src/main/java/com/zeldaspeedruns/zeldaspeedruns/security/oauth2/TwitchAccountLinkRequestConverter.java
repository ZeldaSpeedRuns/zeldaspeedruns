package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

public class TwitchAccountLinkRequestConverter implements Converter<OidcUser, AccountLinkRequest> {
    @Override
    public AccountLinkRequest convert(OidcUser user) {
        String subject = user.getSubject();
        String preferredUsername = user.getPreferredUsername();
        String emailAddress = user.getEmail();
        Boolean emailVerified = user.getEmailVerified();

        var linkRequest = new  AccountLinkRequest(subject, preferredUsername, emailAddress, emailVerified);
        linkRequest.addUsernameCandidate(preferredUsername.replaceAll("[^\\w]", ""));
        return linkRequest;
    }
}
