package com.zeldaspeedruns.zeldaspeedruns.security.user;

import jakarta.persistence.*;
import org.hibernate.annotations.NaturalId;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * User action tokens are one-time use tokens that allow a user to reset their password, confirm their email and so
 * forth through a method of third party verification.
 */
@Entity
@Table(name = "user_action_tokens")
public class UserActionToken {
    public static final int TOKEN_VALUE_LENGTH = 40;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private ZsrUser user;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, updatable = false)
    private ActionType actionType;

    @NaturalId
    @Column(name = "token", length = TOKEN_VALUE_LENGTH, unique = true, nullable = false, updatable = false)
    private String tokenValue;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt = Instant.now().plus(1, ChronoUnit.DAYS);

    @Column(name = "consumed", nullable = false)
    private Boolean consumed = false;

    /** JPA Constructor */
    protected UserActionToken() {
    }

    /**
     * Constructs a UserActionToken entity for a {@link ZsrUser}.
     *
     * @param user The ZsrUser to create the token for.
     * @param actionType The type of action the token will be allowed to take.
     * @param tokenValue The string value of the token.
     */
    public UserActionToken(ZsrUser user, ActionType actionType, String tokenValue) {
        this.user = Objects.requireNonNull(user, "user must not be null");
        this.actionType = Objects.requireNonNull(actionType, "actionType must not be null");
        this.tokenValue = Objects.requireNonNull(tokenValue, "tokenValue must not be null");
    }

    /**
     * Returns the numeric ID for this token.
     *
     * @return The numeric database identity.
     */
    public Long getId() {
        return id;
    }

    /**
     * Returns the {@link ZsrUser} for this token.
     *
     * @return ZsrUser entity.
     */
    public ZsrUser getUser() {
        return user;
    }

    /**
     * Returns the action type for this token.
     *
     * @return The action type.
     */
    public ActionType getActionType() {
        return actionType;
    }

    /**
     * Returns the alphanumeric token value.
     *
     * @return Token value.
     */
    public String getTokenValue() {
        return tokenValue;
    }

    /**
     * Gets the expiration date and time for this token.
     *
     * @return The expiration date and time as an {@link Instant}
     */
    public Instant getExpiresAt() {
        return expiresAt;
    }

    /**
     * Sets the expiration date and time for this token.
     *
     * @param expiresAt The new expiration date and time as a {@link Instant}
     */
    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
    }

    /**
     * Checks if this token has expired by a given time and date.
     *
     * @param now The time and date as an {@link Instant} to check.
     * @return True if the token has expired, False if not.
     */
    public boolean hasExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    /**
     * Checks if the token has been consumed.
     *
     * @return True if consumed, false if not.
     */
    public boolean isConsumed() {
        return consumed;
    }

    /**
     * Consumes the token, making it invalid to use again.
     */
    public void consume() {
        this.consumed = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserActionToken that)) return false;
        return Objects.equals(tokenValue, that.tokenValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenValue);
    }
}
