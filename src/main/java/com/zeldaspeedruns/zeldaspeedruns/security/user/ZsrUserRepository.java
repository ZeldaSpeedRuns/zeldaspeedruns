package com.zeldaspeedruns.zeldaspeedruns.security.user;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZsrUserRepository extends CrudRepository<ZsrUser, Long> {
    Optional<ZsrUser> findByUsernameIgnoreCase(String username);

    Optional<ZsrUser> findByEmailAddressIgnoreCase(String emailAddress);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailAddressIgnoreCase(String emailAddress);
}
