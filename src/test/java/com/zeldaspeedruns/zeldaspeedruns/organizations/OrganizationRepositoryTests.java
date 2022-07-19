package com.zeldaspeedruns.zeldaspeedruns.organizations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("testing")
class OrganizationRepositoryTests {
    @Autowired
    private OrganizationRepository organizationRepository;

    @Autowired
    private TestEntityManager em;

    private Organization zsr;
    private Organization tsg;

    @BeforeEach
    void beforeEach() {
        zsr = em.persistAndFlush(new Organization("ZeldaSpeedRuns" ,"zsr"));
        tsg = em.persistAndFlush(new Organization("The Silver Gauntlets" ,"tsg"));
    }

    @Test
    void findBySlug() {
        var result = organizationRepository.findBySlug("tsg");
        assertThat(result).isNotEmpty().contains(tsg);
    }
}
