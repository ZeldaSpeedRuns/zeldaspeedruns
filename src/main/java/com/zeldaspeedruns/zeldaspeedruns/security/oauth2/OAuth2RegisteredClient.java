package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "oauth2_registered_clients")
public class OAuth2RegisteredClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private final UUID uuid;

    @NaturalId
    @Column(name = "client_id", nullable = false, unique = true)
    private String clientId;

    @Column(name = "client_id_issued_at", nullable = false)
    private Instant clientIdIssuedAt = Instant.now();

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "client_secret_expires_at")
    private Instant clientSecretExpiresAt;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    protected OAuth2RegisteredClient() {
        this.uuid = UUID.randomUUID();
    }

    public OAuth2RegisteredClient(String clientId, String clientName) {
        this.uuid = UUID.randomUUID();
        this.clientId = Objects.requireNonNull(clientId);
        this.clientName = Objects.requireNonNull(clientName);
    }

    public OAuth2RegisteredClient(RegisteredClient registeredClient) {
        this.uuid = UUID.fromString(registeredClient.getId());
        this.clientId = registeredClient.getClientId();
        this.clientIdIssuedAt = registeredClient.getClientIdIssuedAt();
        this.clientSecret = registeredClient.getClientSecret();
        this.clientSecretExpiresAt = registeredClient.getClientSecretExpiresAt();
        this.clientName = registeredClient.getClientName();
    }

    public Long getId() {
        return id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Instant getClientIdIssuedAt() {
        return clientIdIssuedAt;
    }

    public void setClientIdIssuedAt(Instant clientIdIssuedAt) {
        this.clientIdIssuedAt = clientIdIssuedAt;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public Instant getClientSecretExpiresAt() {
        return clientSecretExpiresAt;
    }

    public void setClientSecretExpiresAt(Instant clientSecretExpiresAt) {
        this.clientSecretExpiresAt = clientSecretExpiresAt;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }


}
