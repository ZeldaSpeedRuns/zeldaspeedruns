package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OAuth2AccountLinkRepository extends CrudRepository<OAuth2AccountLink, Long> {
    @Query("""
            SELECT l FROM OAuth2AccountLink l
            JOIN FETCH l.user u
            WHERE l.registrationId = :registrationId AND l.subject = :subject
            """)
    Optional<OAuth2AccountLink> findByRegistrationIdAndSubject(String registrationId, String subject);
}
