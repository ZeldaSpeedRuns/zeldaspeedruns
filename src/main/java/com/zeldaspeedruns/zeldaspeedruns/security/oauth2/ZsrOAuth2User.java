package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class ZsrOAuth2User extends ZsrUserDetails implements OAuth2User {
    private final OAuth2User oAuth2User;

    public ZsrOAuth2User(ZsrUserDetails userDetails, OAuth2User oAuth2User) {
        super(userDetails.getUser(), userDetails.getOrganizationMemberships());
        this.oAuth2User = oAuth2User;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oAuth2User.getAttributes();
    }

    @Override
    public String getName() {
        return getUser().getUsername();
    }
}
