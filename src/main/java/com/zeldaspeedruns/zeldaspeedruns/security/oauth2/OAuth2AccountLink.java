package com.zeldaspeedruns.zeldaspeedruns.security.oauth2;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "oauth2_linked_accounts")
public class OAuth2AccountLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private ZsrUser user;

    @Column(name = "registration_id", nullable = false, updatable = false)
    private String registrationId;

    @Column(name = "subject", nullable = false, updatable = false)
    private String subject;

    @Column(name = "name", nullable = false)
    private String name;

    protected OAuth2AccountLink() {
    }

    public OAuth2AccountLink(ZsrUser user, String registrationId, String subject, String name) {
        this.user = Objects.requireNonNull(user, "user must not be null");
        this.registrationId = Objects.requireNonNull(registrationId, "registrationId must not be null");
        this.subject = Objects.requireNonNull(subject, "subject must not be null");
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    public Long getId() {
        return id;
    }

    public ZsrUser getUser() {
        return user;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getSubject() {
        return subject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "name must not be null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OAuth2AccountLink that)) return false;
        return registrationId.equals(that.registrationId) && subject.equals(that.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationId, subject);
    }
}
