package com.zeldaspeedruns.zeldaspeedruns.security.user;

import com.zeldaspeedruns.zeldaspeedruns.core.AbstractUuidEntity;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "user_recovery_tokens")
@ToString(onlyExplicitlyIncluded = true)
public class RecoveryToken extends AbstractUuidEntity {
    public static final int TOKEN_VALUE_LENGTH = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false, updatable = false)
    @ToString.Include
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, updatable = false)
    private ZsrUser user;

    @Column(name = "token_value", nullable = false, unique = true, length = TOKEN_VALUE_LENGTH)
    @ToString.Include
    private String tokenValue;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);

    @Column(name = "consumed", nullable = false)
    private Boolean consumed = false;

    @Column(name = "consumed_at")
    private Instant consumedAt = null;

    protected RecoveryToken() {
    }

    public RecoveryToken(ZsrUser user, String tokenValue) {
        this.user = Objects.requireNonNull(user, "user must not be null");
        this.tokenValue = Objects.requireNonNull(tokenValue, "tokenValue must not be null");
    }

    public RecoveryToken(ZsrUser user, String tokenValue, Instant expiresAt) {
        this(user, tokenValue);
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
    }

    public Long getId() {
        return id;
    }

    public ZsrUser getUser() {
        return user;
    }

    public void setUser(ZsrUser user) {
        this.user = Objects.requireNonNull(user, "user must not be null");
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = Objects.requireNonNull(tokenValue, "tokenValue must not be null");
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public Optional<Instant> getConsumedAt() {
        return Optional.ofNullable(consumedAt);
    }

    public void setConsumedAt(@Nullable Instant consumedAt) {
        this.consumedAt = consumedAt;
    }

    public boolean isValid(Instant now) {
        return !isConsumed() && now.isBefore(expiresAt);
    }
}
