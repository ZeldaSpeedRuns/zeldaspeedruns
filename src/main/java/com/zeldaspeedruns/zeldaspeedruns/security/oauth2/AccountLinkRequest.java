package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import groovyjarjarantlr4.v4.runtime.misc.OrderedHashSet;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.util.Objects;
import java.util.Set;

public class AccountLinkRequest {
    private final String subject;
    private final String preferredUsername;
    private final Set<String> usernameCandidates = new OrderedHashSet<>();
    private final String emailAddress;
    private final boolean emailVerified;

    public AccountLinkRequest(String subject, String preferredUsername, String emailAddress, boolean emailVerified) {
        this.subject = Objects.requireNonNull(subject, "subject must not be null");
        this.preferredUsername = Objects.requireNonNull(preferredUsername, "preferredUsername must not be null");
        this.emailAddress = Objects.requireNonNull(emailAddress, "emailAddress must not be null");
        this.emailVerified = emailVerified;
        addUsernameCandidate(preferredUsername);
    }

    public void addUsernameCandidate(String usernameCandidate) {
        this.usernameCandidates.add(usernameCandidate);
    }

    public String getSubject() {
        return subject;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public Set<String> getUsernameCandidates() {
        return Set.copyOf(usernameCandidates);
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }
}
