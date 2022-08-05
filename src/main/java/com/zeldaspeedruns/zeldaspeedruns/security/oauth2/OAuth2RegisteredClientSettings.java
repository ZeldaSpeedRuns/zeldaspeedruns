package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@RedisHash("oauth2_registered_client_settings")
public class OAuth2RegisteredClientSettings implements Serializable {
    @Id
    private final UUID clientUuid;

    private Set<ClientAuthenticationMethod> authenticationMethods = new HashSet<>();
    private Set<AuthorizationGrantType> authorizationGrantTypes = new HashSet<>();
    private Set<String> redirectUris = new HashSet<>();
    private Set<String> clientScopes = new HashSet<>();

    private ClientSettings clientSettings = ClientSettings.builder().build();
    private TokenSettings tokenSettings = TokenSettings.builder().build();

    public OAuth2RegisteredClientSettings(UUID clientUuid) {
        this.clientUuid = clientUuid;
    }

    public OAuth2RegisteredClientSettings(RegisteredClient registeredClient) {
        this.clientUuid = UUID.fromString(registeredClient.getId());
        this.authenticationMethods = registeredClient.getClientAuthenticationMethods();
        this.authorizationGrantTypes = registeredClient.getAuthorizationGrantTypes();
        this.redirectUris = registeredClient.getRedirectUris();
        this.clientScopes = registeredClient.getScopes();
        this.clientSettings = registeredClient.getClientSettings();
        this.tokenSettings = registeredClient.getTokenSettings();
    }

    public UUID getClientUuid() {
        return clientUuid;
    }

    public Set<ClientAuthenticationMethod> getAuthenticationMethods() {
        return authenticationMethods;
    }

    public Set<AuthorizationGrantType> getAuthorizationGrantTypes() {
        return authorizationGrantTypes;
    }

    public Set<String> getRedirectUris() {
        return redirectUris;
    }

    public Set<String> getClientScopes() {
        return clientScopes;
    }

    public ClientSettings getClientSettings() {
        return clientSettings;
    }

    public TokenSettings getTokenSettings() {
        return tokenSettings;
    }
}
