package com.zeldaspeedruns.zeldaspeedruns.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AbstractUuidEntityTests {
    @Test
    void equals_whenUuidMatches_isTrue() {
        AbstractUuidEntity a = new AbstractUuidEntity() {
            // intentionally empty
        };

        AbstractUuidEntity b = new AbstractUuidEntity() {
            // intentionally empty
        };

        b.setUuid(a.getUuid());
        assertEquals(a, b);
    }

    @Test
    void equals_whenUuidDoesNotMatch_isFalse() {
        AbstractUuidEntity a = new AbstractUuidEntity() {
            // intentionally empty
        };

        AbstractUuidEntity b = new AbstractUuidEntity() {
            // intentionally empty
        };

        assertNotEquals(a, b);
    }

    @Test
    void hashCode_whenUuidMatches_isEqual () {
        AbstractUuidEntity a = new AbstractUuidEntity() {
            // intentionally empty
        };

        AbstractUuidEntity b = new AbstractUuidEntity() {
            // intentionally empty
        };

        b.setUuid(a.getUuid());
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void hashCode_whenUuidDoesNotMatch_isNotEqual() {
        AbstractUuidEntity a = new AbstractUuidEntity() {
            // intentionally empty
        };

        AbstractUuidEntity b = new AbstractUuidEntity() {
            // intentionally empty
        };

        assertNotEquals(a.hashCode(), b.hashCode());
    }
}
