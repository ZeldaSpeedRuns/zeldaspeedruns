package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OAuth2RegisteredClientSettingsRepository extends CrudRepository<OAuth2RegisteredClientSettings, UUID> {
}
