package com.zeldaspeedruns.zeldaspeedruns.security.user;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.util.Objects;
import java.util.UUID;

/**
 * User database entity.
 */
@Entity
@Table(name = "users")
public class ZsrUser {
    @Column(name = "uuid", nullable = false, unique = true, updatable = false)
    private final UUID uuid = UUID.randomUUID();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NaturalId
    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email_address", nullable = false, unique = true)
    private String emailAddress;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_superuser", nullable = false)
    private Boolean superuser = false;

    @Column(name = "is_enabled", nullable = false)
    private Boolean enabled = true;

    /**
     * JPA constructor
     */
    protected ZsrUser() {
    }

    /**
     * Constructs a new user instance.
     *
     * @param username     The username for the user.
     * @param emailAddress The email address for the user.
     * @param password     The encrypted password for the user.
     */
    public ZsrUser(String username, String emailAddress, String password) {
        this.username = Objects.requireNonNull(username, "username must not be null");
        this.emailAddress = Objects.requireNonNull(emailAddress, "emailAddress must not be null");
        this.password = Objects.requireNonNull(password, "password must not be null");
    }

    /**
     * Gets the numeric database ID for this user.
     *
     * @return Numeric identity
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the UUID for this user.
     *
     * @return UUID identifying this user
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Gets the username for this user.
     *
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for this user.
     *
     * @param username The new username, must not be null
     */
    public void setUsername(String username) {
        this.username = Objects.requireNonNull(username, "username must not be null");
    }

    /**
     * Gets the email address for this user.
     *
     * @return The email address
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the email address for this user.
     *
     * @param emailAddress The new email address, must not be null
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = Objects.requireNonNull(emailAddress, "emailAddress must not be null");
    }

    /**
     * Gets the encrypted password for this user.
     *
     * @return The encrypted password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for this user.
     *
     * @param password Encrypted password
     */
    public void setPassword(String password) {
        this.password = Objects.requireNonNull(password, "password must not be null");
    }

    /**
     * Checks whether this user is a superuser.
     *
     * @return true if this user is a superuser
     */
    public boolean isSuperuser() {
        return superuser;
    }

    /**
     * Sets superuser status for this user.
     *
     * @param superuser The new superuser status of this user
     */
    public void setSuperuser(boolean superuser) {
        this.superuser = superuser;
    }

    /**
     * Checks whether this user
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets whether this user is enabled.
     *
     * @param enabled The new status
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ZsrUser zsrUser)) return false;
        return Objects.equals(uuid, zsrUser.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
