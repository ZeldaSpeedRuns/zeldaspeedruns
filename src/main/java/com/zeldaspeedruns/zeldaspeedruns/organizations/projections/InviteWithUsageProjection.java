package com.zeldaspeedruns.zeldaspeedruns.organizations.projections;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;

@Data
@AllArgsConstructor
public class InviteWithUsageProjection {
    private Long id;
    private String code;
    private OffsetDateTime expiresAt;
    private Long maxUses;
    private Long uses;
    private boolean invalidated;

    public Duration getExpiresIn() {
        return Duration.between(Instant.now(), expiresAt);
    }
}
