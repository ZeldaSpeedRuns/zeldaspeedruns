package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import com.zeldaspeedruns.zeldaspeedruns.security.PasswordUtils;
import com.zeldaspeedruns.zeldaspeedruns.security.user.*;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

@Service
public class ZsrOidcUserService extends OidcUserService {
    private final OAuth2AccountLinkRepository linkRepository;
    private final ZsrUserService userService;

    public ZsrOidcUserService(OAuth2AccountLinkRepository linkRepository,
                              ZsrUserService userService) {
        this.linkRepository = linkRepository;
        this.userService = userService;
    }

    protected ZsrUser createUserFromRequest(OidcUserRequest userRequest) {
        String username = userRequest.getIdToken().getPreferredUsername();
        String emailAddress = userRequest.getIdToken().getEmail();
        String password = PasswordUtils.generatePassword(40);

        try {
            return userService.createUser(username, emailAddress, password);
        } catch (UsernameInUseException | EmailInUseException e) {
            throw new RuntimeException(e);
        }
    }

    protected OAuth2AccountLink createLinkForUser(ZsrUser user, OidcUserRequest userRequest) {
        var registrationId = userRequest.getClientRegistration().getRegistrationId();
        var subject = userRequest.getIdToken().getSubject();
        var name = userRequest.getIdToken().getPreferredUsername();

        return linkRepository.save(new OAuth2AccountLink(user, registrationId, subject, name));
    }

    protected OAuth2AccountLink linkAccount(OidcUserRequest userRequest) {
        var emailAddress = userRequest.getIdToken().getEmail();
        var emailVerified = userRequest.getIdToken().getEmailVerified();

        ZsrUser user;

        try {
            if (emailVerified) {
                user = userService.loadByEmailAddress(emailAddress);
            } else {
                throw new OAuth2AuthenticationException("cannot link account, email not verified");
            }
        } catch (EmailNotFoundException e) {
            user = createUserFromRequest(userRequest);
        }

        return createLinkForUser(user, userRequest);
    }

    protected OAuth2AccountLink resolveUserRequest(OidcUserRequest userRequest) {
        var registrationId = userRequest.getClientRegistration().getRegistrationId();
        var subject = userRequest.getIdToken().getSubject();
        var linkOptional = linkRepository.findByRegistrationIdAndSubject(registrationId, subject);

        if (linkOptional.isEmpty()) {
            return linkAccount(userRequest);
        } else {
            return linkOptional.get();
        }
    }

    @Override
    public ZsrOidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        var oidcUser = super.loadUser(userRequest);
        var linkedAccount = resolveUserRequest(userRequest);
        return new ZsrOidcUser(linkedAccount.getUser(), oidcUser);
    }
}
