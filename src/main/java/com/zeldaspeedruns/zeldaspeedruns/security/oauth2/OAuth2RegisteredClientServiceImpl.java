package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import com.zeldaspeedruns.zeldaspeedruns.security.SecureTokenUtils;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
public class OAuth2RegisteredClientServiceImpl implements OAuth2RegisteredClientService {
    private final OAuth2RegisteredClientRepository clientRepository;
    private final OAuth2RegisteredClientSettingsRepository settingsRepository;

    public OAuth2RegisteredClientServiceImpl(OAuth2RegisteredClientRepository clientRepository,
                                             OAuth2RegisteredClientSettingsRepository settingsRepository) {
        this.clientRepository = clientRepository;
        this.settingsRepository = settingsRepository;
    }

    @Override
    public Iterable<OAuth2RegisteredClient> getRegisteredClientsByUser(ZsrUser user) {
        return clientRepository.findByUser(user);
    }

    @Override
    @Transactional
    public OAuth2RegisteredClient createClientForUser(ZsrUser user, String name, Set<String> redirectUrls) {
        var registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientName(name)
                .clientId(SecureTokenUtils.generateAlphanumericToken(30))
                .clientIdIssuedAt(Instant.now())
                .clientSecret(SecureTokenUtils.generateAlphanumericToken(30))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUris(uris -> uris.addAll(redirectUrls))
                .scope(OidcScopes.OPENID)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();

        var client = clientRepository.save(new OAuth2RegisteredClient(registeredClient));
        settingsRepository.save(new OAuth2RegisteredClientSettings(registeredClient));
        client.setUser(user);

        return client;
    }
}
