package com.zeldaspeedruns.zeldaspeedruns.organizations.projections;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class InviteWithUsageCount {
    private Long id;
    private UUID uuid;
    private OffsetDateTime expiresAt;
    private Long maxUses;
    private Long uses;
    private boolean invalidated;

    public Duration getExpiresIn() {
        return Duration.between(Instant.now(), expiresAt);
    }
}
