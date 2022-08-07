package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;

import java.util.Set;

public interface OAuth2RegisteredClientService {
    Iterable<OAuth2RegisteredClient> getRegisteredClientsByUser(ZsrUser user);

    OAuth2RegisteredClient createClientForUser(ZsrUser user, String name, Set<String> redirectUrls);
}
