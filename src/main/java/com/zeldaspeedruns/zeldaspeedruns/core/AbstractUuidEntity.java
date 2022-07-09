package com.zeldaspeedruns.zeldaspeedruns.core;

import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public abstract class AbstractUuidEntity implements UuidEntity {
    @Column(name = "uuid", unique = true, nullable = false, updatable = false)
    @ToString.Include
    private UUID uuid = UUID.randomUUID();

    public UUID getUuid() {
        return uuid;
    }

    protected void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractUuidEntity entity)) return false;
        return uuid.equals(entity.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }
}
