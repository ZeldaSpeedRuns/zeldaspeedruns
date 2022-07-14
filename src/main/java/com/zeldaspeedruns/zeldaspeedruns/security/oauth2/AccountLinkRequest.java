package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import groovyjarjarantlr4.v4.runtime.misc.OrderedHashSet;

import java.util.Objects;
import java.util.Set;

public class AccountLinkRequest {
    private final String subject;
    private final String username;
    private final Set<String> usernameCandidates = new OrderedHashSet<>();
    private final String emailAddress;
    private final boolean emailVerified;

    public AccountLinkRequest(String subject, String username, String emailAddress, boolean emailVerified) {
        this.subject = Objects.requireNonNull(subject, "subject must not be null");
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.emailAddress = Objects.requireNonNull(emailAddress, "emailAddress must not be null");
        this.emailVerified = emailVerified;
    }

    public void addUsernameCandidate(String usernameCandidate) {
        this.usernameCandidates.add(usernameCandidate);
    }

    public String getSubject() {
        return subject;
    }

    public String getUsername() {
        return username;
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
