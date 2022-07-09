package com.zeldaspeedruns.zeldaspeedruns.security.user;

import com.zeldaspeedruns.zeldaspeedruns.core.AbstractUuidEntity;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
@ToString(onlyExplicitlyIncluded = true)
public class ZsrUser extends AbstractUuidEntity {
    public final static int USERNAME_MAX_LENGTH = 32;
    public final static int EMAIL_MAX_LENGTH = 128;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    @ToString.Include
    private Long id;

    @NaturalId(mutable = true)
    @Column(name = "username", unique = true, nullable = false, length = USERNAME_MAX_LENGTH)
    @ToString.Include
    private String username;

    @Column(name = "email_address", unique = true, length = EMAIL_MAX_LENGTH)
    private String emailAddress;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "is_administrator", nullable = false)
    private Boolean administrator = false;

    protected ZsrUser() {
    }

    public ZsrUser(String username, String emailAddress, String password) {
        this.username = Objects.requireNonNull(username, "username must not be null");
        ;
        this.emailAddress = Objects.requireNonNull(emailAddress, "email address must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = Objects.requireNonNull(username, "username must not be null");
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = Objects.requireNonNull(emailAddress, "email address must not be null");
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Objects.requireNonNull(password, "password must not be null");
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isAdministrator() {
        return administrator;
    }

    public void setAdministrator(boolean administrator) {
        this.administrator = administrator;
    }
}
