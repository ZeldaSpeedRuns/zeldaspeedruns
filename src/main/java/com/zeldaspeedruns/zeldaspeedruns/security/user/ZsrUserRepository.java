package com.zeldaspeedruns.zeldaspeedruns.security.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ZsrUserRepository extends CrudRepository<ZsrUser, Long> {
    Optional<ZsrUser> findByUsername(String username);

    Optional<ZsrUser> findByEmailAddress(String emailAddress);

    boolean existsByUsername(String username);

    boolean existsByEmailAddress(String emailAddress);
}
