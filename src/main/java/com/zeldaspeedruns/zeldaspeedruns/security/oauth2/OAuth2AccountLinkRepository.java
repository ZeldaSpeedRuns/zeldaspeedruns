package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OAuth2AccountLinkRepository extends CrudRepository<OAuth2AccountLink, Long> {
    @EntityGraph("OAuth2AccountLink.user")
    Optional<OAuth2AccountLink> findByRegistrationIdAndSubject(String registrationId, String subject);

    List<OAuth2AccountLink> findAllByUser(ZsrUser user);
}
