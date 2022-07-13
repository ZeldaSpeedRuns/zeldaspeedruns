package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Objects;

public class DiscordAccountLinkRequestConverter implements Converter<OAuth2User, AccountLinkRequest> {
    private static final String SUBJECT_ATTRIBUTE = "id";
    private static final String PREFERRED_USERNAME_ATTRIBUTE = "username";
    private static final String EMAIL_ADDRESS_ATTRIBUTE = "email";
    private static final String EMAIL_VERIFIED_ATTRIBUTE = "verified";
    private static final String DISCRIMINATOR_ATTRIBUTE = "discriminator";

    @Override
    public AccountLinkRequest convert(OAuth2User user) {
        String subject = getAttribute(user, SUBJECT_ATTRIBUTE);
        String preferredUsername = getAttribute(user, PREFERRED_USERNAME_ATTRIBUTE);
        String emailAddress = getAttribute(user, EMAIL_ADDRESS_ATTRIBUTE);
        Boolean emailVerified = getAttribute(user, EMAIL_VERIFIED_ATTRIBUTE);
        String discriminator = getAttribute(user, DISCRIMINATOR_ATTRIBUTE);

        // Sanitize the username and set up the link request.
        preferredUsername = preferredUsername.replaceAll("[^\\w]", "");
        var linkRequest = new AccountLinkRequest(subject, preferredUsername, emailAddress, emailVerified);
        linkRequest.addUsernameCandidate(preferredUsername + discriminator);
        linkRequest.addUsernameCandidate(preferredUsername + '-' + discriminator);
        return linkRequest;
    }

    private <T> T getAttribute(OAuth2User user, String attribute) {
        return Objects.requireNonNull(user.getAttribute(attribute));
    }
}
