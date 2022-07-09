package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class ZsrOidcUserService extends OidcUserService {
    @Override
    public ZsrOidcUserDetails loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        return new ZsrOidcUserDetails(null, super.loadUser(userRequest));
    }
}
