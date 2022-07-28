package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserTestUtils;
import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrganizationPermissionEvaluatorTests {
    @Mock
    private OrganizationService organizationService;

    @InjectMocks
    private OrganizationPermissionEvaluator evaluator;

    private Organization organization;
    private ZsrUser user;
    private ZsrUserDetails userDetails;
    private TestingAuthenticationToken authentication;

    @BeforeEach
    void beforeEach() {
        organization = new Organization("ZeldaSpeedRuns", "zsr");
        user = ZsrUserTestUtils.zsrUser("spell");
        userDetails = new ZsrUserDetails(user);

        authentication = new TestingAuthenticationToken(userDetails, null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
    }

    @Test
    void hasPermission() {
        when(organizationService.findMembership(organization, user)).thenAnswer(invocation -> {
            var member = new OrganizationMember(organization, user);
            member.getRoles().add(new OrganizationRole(organization, "Restreamer", "restreamer"));
            return Optional.of(member);
        });

        assertTrue(evaluator.hasPermission(authentication, organization, "restreamer"));
    }

    @Test
    void hasPermission_whenDoesNotHaveRole_isFalse() {
        when(organizationService.findMembership(organization, user)).thenAnswer(invocation -> {
            var member = new OrganizationMember(organization, user);
            member.getRoles().add(new OrganizationRole(organization, "Restreamer", "restreamer"));
            return Optional.of(member);
        });

        assertFalse(evaluator.hasPermission(authentication, organization, "tracker"));
    }

    @Test
    void hasPermission_whenMemberNotFound_isFalse() {
        when(organizationService.findMembership(organization, user)).thenReturn(Optional.empty());
        assertFalse(evaluator.hasPermission(authentication, organization, "role"));
    }
}
