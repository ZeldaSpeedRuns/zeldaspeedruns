package com.zeldaspeedruns.zeldaspeedruns.security.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserActionTokenRepository extends CrudRepository<UserActionToken, Long> {
    Optional<UserActionToken> findByTokenValue(String tokenValue);
}
