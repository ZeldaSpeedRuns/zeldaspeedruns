package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OAuth2RegisteredClientRepository extends JpaRepository<OAuth2RegisteredClient, Long> {
    Optional<OAuth2RegisteredClient> findByUuid(UUID uuid);

    Optional<OAuth2RegisteredClient> findByClientId(String clientId);
}
