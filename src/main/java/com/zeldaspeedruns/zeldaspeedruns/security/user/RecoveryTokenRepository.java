package com.zeldaspeedruns.zeldaspeedruns.security.user;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RecoveryTokenRepository extends CrudRepository<RecoveryToken, Long> {
    Optional<RecoveryToken> findByTokenValue(String tokenValue);
}
