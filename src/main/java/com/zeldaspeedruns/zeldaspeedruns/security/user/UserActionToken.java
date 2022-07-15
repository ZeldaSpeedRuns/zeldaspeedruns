package com.zeldaspeedruns.zeldaspeedruns.security.user;

import com.vladmihalcea.hibernate.type.basic.PostgreSQLEnumType;
import org.hibernate.annotations.NaturalId;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Entity
@Table(name = "user_action_tokens")
@TypeDef(
        name = "pgsql_enum",
        typeClass = PostgreSQLEnumType.class
)
public class UserActionToken {
    public static final int TOKEN_VALUE_LENGTH = 40;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private ZsrUser user;

    @Type(type = "pgsql_enum")
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

    protected UserActionToken() {
    }

    public UserActionToken(ZsrUser user, ActionType actionType, String tokenValue) {
        this.user = Objects.requireNonNull(user, "user must not be null");
        this.actionType = Objects.requireNonNull(actionType, "actionType must not be null");
        this.tokenValue = Objects.requireNonNull(tokenValue, "tokenValue must not be null");
    }

    public Long getId() {
        return id;
    }

    public ZsrUser getUser() {
        return user;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public String getTokenValue() {
        return tokenValue;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
    }

    public boolean hasExpired(Instant now) {
        return now.isAfter(expiresAt);
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
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
