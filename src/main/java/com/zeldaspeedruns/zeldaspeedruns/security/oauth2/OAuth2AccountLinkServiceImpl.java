package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import com.zeldaspeedruns.zeldaspeedruns.security.PasswordUtils;
import com.zeldaspeedruns.zeldaspeedruns.security.user.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class OAuth2AccountLinkServiceImpl implements OAuth2AccountLinkService {
    private final static Logger logger = LoggerFactory.getLogger(OAuth2AccountLinkServiceImpl.class);

    private final ZsrUserService userService;
    private final OAuth2AccountLinkRepository linkRepository;

    private final Map<String, Converter<OAuth2User, AccountLinkRequest>> oAuthUserConverters = new HashMap<>();
    private final Map<String, Converter<OidcUser, AccountLinkRequest>> oidcUserConverters = new HashMap<>();

    public OAuth2AccountLinkServiceImpl(ZsrUserService userService,
                                        OAuth2AccountLinkRepository linkRepository) {
        this.userService = userService;
        this.linkRepository = linkRepository;

        // Register our converters.
        oAuthUserConverters.put("discord", new DiscordAccountLinkRequestConverter());
        oidcUserConverters.put("twitch", new TwitchAccountLinkRequestConverter());
    }

    protected ZsrUser createUserFromRequest(AccountLinkRequest request) {
        String password = PasswordUtils.generatePassword(40);

        for (var candidate : request.getUsernameCandidates()) {
            try {
                return userService.createUser(candidate, request.getEmailAddress(), password);
            } catch (UsernameInUseException | EmailInUseException ignored) {
                logger.debug("could not create user with candidate username {}", candidate);
            }
        }

        throw new OAuth2AuthenticationException("could not create user account");
    }

    protected OAuth2AccountLink linkAccount(AccountLinkRequest request, ClientRegistration registration, boolean verifiedOnly, boolean createUser) {
        ZsrUser user;

        logger.debug("attempting account link for registration {} with subject {}",
                registration.getRegistrationId(), request.getSubject());

        try {
            if (!verifiedOnly || request.isEmailVerified()) {
                user = userService.loadByEmailAddress(request.getEmailAddress());
                logger.debug("found existing user for registration {} with subject {}",
                        registration.getRegistrationId(), request.getSubject());

            } else {
                throw new OAuth2AuthenticationException("cannot link because email is not verified");
            }
        } catch (EmailNotFoundException e) {
            if (createUser) {
                logger.debug("attempting to create user for registration {} with subject {}",
                        registration.getRegistrationId(), request.getSubject());
                request.addUsernameCandidate(request.getPreferredUsername() + "_" + registration.getRegistrationId());
                user = createUserFromRequest(request);
                logger.debug("created new user {}", user.getUsername());
            } else {
                throw new OAuth2AuthenticationException("could not find user");
            }
        }

        var link = new OAuth2AccountLink(
                user,
                registration.getRegistrationId(),
                request.getSubject(),
                request.getPreferredUsername());

        logger.debug("created new account link for user {} to registration {}",
                user.getUsername(), registration.getRegistrationId());

        return linkRepository.save(link);
    }

    protected Optional<OAuth2AccountLink> loadAccountLink(AccountLinkRequest request, ClientRegistration registration) {
        var optional = linkRepository.findByRegistrationIdAndSubject(registration.getRegistrationId(), request.getSubject());

        if (optional.isPresent()) {
            var link = optional.get();
            logger.debug("found matching account link for registration {} matching user {}",
                    registration.getRegistrationId(), link.getUser().getUsername());

            link.setName(request.getPreferredUsername());
        }

        return optional;
    }

    @Override
    public OAuth2AccountLink processUser(OidcUser user, ClientRegistration registration) throws OAuth2AuthenticationException {
        var converter = oidcUserConverters.get(registration.getRegistrationId());
        if (converter == null) {
            throw new OAuth2AuthenticationException(String.format("no user converter for client registration '%s'", registration.getRegistrationId()));
        }

        var linkRequest = converter.convert(user);
        if (linkRequest == null) {
            throw new OAuth2AuthenticationException("could not convert OpenID Connect user");
        }

        logger.debug("processing OpenID Connect link request for registration {} with user id {}",
                registration.getRegistrationId(), linkRequest.getSubject());

        return loadAccountLink(linkRequest, registration)
                .orElseGet(() ->linkAccount(linkRequest, registration, true, true));
    }

    @Override
    public OAuth2AccountLink processUser(OAuth2User user, ClientRegistration registration) throws OAuth2AuthenticationException {
        var converter = oAuthUserConverters.get(registration.getRegistrationId());
        if (converter == null) {
            throw new OAuth2AuthenticationException(String.format("no user converter for client registration '%s'", registration.getRegistrationId()));
        }

        var linkRequest = converter.convert(user);
        if (linkRequest == null) {
            throw new OAuth2AuthenticationException("could not convert OAuth2 user");
        }

        logger.debug("processing OAuth2 link request for registration {} with user id {}",
                registration.getRegistrationId(), linkRequest.getSubject());

        return loadAccountLink(linkRequest, registration)
                .orElseGet(() ->linkAccount(linkRequest, registration, true, true));
    }
}
