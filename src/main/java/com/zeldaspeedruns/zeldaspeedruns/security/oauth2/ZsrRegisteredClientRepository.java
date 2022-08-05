package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ZsrRegisteredClientRepository implements RegisteredClientRepository {
    private final OAuth2RegisteredClientRepository clientRepository;
    private final OAuth2RegisteredClientSettingsRepository settingsRepository;

    public ZsrRegisteredClientRepository(OAuth2RegisteredClientRepository clientRepository,
                                         OAuth2RegisteredClientSettingsRepository settingsRepository) {
        this.clientRepository = clientRepository;
        this.settingsRepository = settingsRepository;
    }

    @Override
    @Transactional
    public void save(RegisteredClient registeredClient) {
        clientRepository.save(new OAuth2RegisteredClient(registeredClient));
        settingsRepository.save(new OAuth2RegisteredClientSettings(registeredClient));
    }

    @Override
    public RegisteredClient findById(String id) {
        var uuid = UUID.fromString(id);
        var client = clientRepository.findByUuid(uuid).orElse(null);
        var settings = settingsRepository.findById(uuid).orElse(null);
        return toRegisteredClient(client, settings);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        var client = clientRepository.findByClientId(clientId).orElse(null);
        if (client != null) {
            var settings = settingsRepository.findById(client.getUuid()).orElse(null);
            return toRegisteredClient(client, settings);
        } else {
            return null;
        }
    }

    private RegisteredClient toRegisteredClient(OAuth2RegisteredClient client, OAuth2RegisteredClientSettings settings) {
        if (client == null || settings == null) {
            return null;
        }

        return RegisteredClient.withId(client.getUuid().toString())
                .clientId(client.getClientId())
                .clientIdIssuedAt(client.getClientIdIssuedAt())
                .clientSecret(client.getClientSecret())
                .clientSecretExpiresAt(client.getClientSecretExpiresAt())
                .clientName(client.getClientName())
                .clientAuthenticationMethods(methods -> methods.addAll(settings.getAuthenticationMethods()))
                .authorizationGrantTypes(grants -> grants.addAll(settings.getAuthorizationGrantTypes()))
                .redirectUris(uris -> uris.addAll(settings.getRedirectUris()))
                .scopes(scopes -> scopes.addAll(settings.getClientScopes()))
                .clientSettings(settings.getClientSettings())
                .tokenSettings(settings.getTokenSettings())
                .build();
    }
}
