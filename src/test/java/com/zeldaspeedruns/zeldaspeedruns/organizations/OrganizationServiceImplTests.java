package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceImplTests {
    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationMemberRepository memberRepository;

    @Mock
    private OrganizationInviteRepository inviteRepository;

    @Mock
    private OrganizationInviteUseRepository inviteUseRepository;

    @InjectMocks
    private OrganizationServiceImpl organizationService;

    @Test
    void createOrganization() {
        when(organizationRepository.save(any(Organization.class))).then(returnsFirstArg());

        var organization = assertDoesNotThrow(() -> organizationService.createOrganization("ZeldaSpeedRuns", "zsr"));
        assertEquals("ZeldaSpeedRuns", organization.getName());
        assertEquals("zsr", organization.getSlug());
        assertEquals(3, organization.getRoles().size());

        verify(organizationRepository, atLeastOnce()).save(organization);
    }

    @Test
    void createOrganization_whenSlugExists_throwsSlugInUseException() {
        when(organizationRepository.existsBySlug(anyString())).thenReturn(true);

        assertThrows(
                OrganizationSlugInUseException.class,
                () -> organizationService.createOrganization("ZeldaSpeedRuns", "zsr"));

        verify(organizationRepository, never()).save(any(Organization.class));
    }

    @Test
    void addOrganizationMember() {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");
        var user = ZsrUserTestUtils.zsrUser("spell");

        when(memberRepository.existsByOrganizationAndUser(organization, user)).thenReturn(false);
        when(memberRepository.save(any(OrganizationMember.class))).then(returnsFirstArg());

        var member = assertDoesNotThrow(() -> organizationService.addOrganizationMember(organization, user));

        verify(memberRepository, atLeastOnce()).save(member);
        assertEquals(organization, member.getOrganization());
        assertEquals(user, member.getUser());
    }

    @Test
    void addOrganizationMember_whenMember_throwsMembershipExistsException() {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");
        var user = ZsrUserTestUtils.zsrUser("spell");

        when(memberRepository.existsByOrganizationAndUser(organization, user)).thenReturn(true);

        assertThrows(
                MembershipExistsException.class,
                () -> organizationService.addOrganizationMember(organization, user));

        verify(memberRepository, never()).save(any(OrganizationMember.class));
    }

    @Test
    void deleteOrganization() {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");
        organizationService.deleteOrganization(organization);
        verify(organizationRepository, atLeastOnce()).delete(organization);
    }

    @Test
    void findOrganizationBySlug() {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");
        when(organizationRepository.findBySlug("zsr")).thenReturn(Optional.of(organization));

        var returned = organizationService.findOrganizationBySlug("zsr");
        assertTrue(returned.isPresent());
        assertEquals(organization, returned.get());
    }

    @Test
    void findOrganizationBySlug_whenNotFound_isEmpty() {
        when(organizationRepository.findBySlug(anyString())).thenReturn(Optional.empty());

        var returned = organizationService.findOrganizationBySlug("zsr");
        assertTrue(returned.isEmpty());
    }

    @Test
    void findAllOrganizations() {
        var organizations = List.of(
                OrganizationTestUtils.organization("ZeldaSpeedRuns"),
                OrganizationTestUtils.organization("The Silver Gauntlets")
        );

        when(organizationRepository.findAll(any(Pageable.class)))
                .then(i -> new PageImpl<>(organizations, i.getArgument(0), organizations.size()));

        var page = organizationService.findAllOrganizations(PageRequest.ofSize(10));
        assertEquals(organizations, page.getContent());
        assertEquals(2, page.getTotalElements());
        assertEquals(10, page.getSize());
    }

    @Test
    void findAllMembersByOrganization() {
        var user1 = ZsrUserTestUtils.zsrUser("spell");
        var user2 = ZsrUserTestUtils.zsrUser("TreZc0_");
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");

        var members = List.of(
                new OrganizationMember(organization, user1),
                new OrganizationMember(organization, user2)
        );

        when(memberRepository.findAllByOrganization(eq(organization), any(Pageable.class)))
                .then(i -> new PageImpl<>(members, i.getArgument(1), members.size()));

        var page = organizationService.findAllMembersByOrganization(organization, PageRequest.ofSize(10));
        assertEquals(members, page.getContent());
        assertEquals(2, page.getTotalElements());
        assertEquals(10, page.getSize());
    }

    @Test
    void findAllMembershipsByUser() {
        var user = ZsrUserTestUtils.zsrUser("spell");
        var memberships = List.of(
                new OrganizationMember(OrganizationTestUtils.organization("ZeldaSpeedRuns"), user),
                new OrganizationMember(OrganizationTestUtils.organization("The Silver Gauntlets"), user)
        );

        when(memberRepository.findAllByUser(user)).thenReturn(memberships);
        var returned = organizationService.findAllMembershipsByUser(user);
        assertEquals(memberships, returned);
    }

    @Test
    void findMembership() {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");
        var user = ZsrUserTestUtils.zsrUser("spell");
        var member = new OrganizationMember(organization, user);
        when(memberRepository.findByUserAndOrganization(user, organization)).thenReturn(Optional.of(member));

        var returned = organizationService.findMembership(organization, user);
        assertTrue(returned.isPresent());
        assertEquals(member, returned.get());
    }

    @Test
    void findMembership_whenNotFound_isEmpty() {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");
        var user = ZsrUserTestUtils.zsrUser("spell");
        when(memberRepository.findByUserAndOrganization(user, organization)).thenReturn(Optional.empty());

        var returned = organizationService.findMembership(organization, user);
        assertTrue(returned.isEmpty());
    }

    @Test
    void createInvite() {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");
        var user = ZsrUserTestUtils.zsrUser("spell");

        when(inviteRepository.save(any(OrganizationInvite.class))).then(returnsFirstArg());

        var invite = organizationService.createInvite(organization, user);

        verify(inviteRepository, times(1)).save(invite);
        assertEquals(invite.getOrganization(), organization);
        assertEquals(invite.getUser(), user);
    }

    @Test
    void deleteInvite() {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");
        var user = ZsrUserTestUtils.zsrUser("spell");
        var invite = new OrganizationInvite(organization, user);

        organizationService.deleteInvite(invite);
        assertTrue(invite.isInvalidated());
    }

    @Test
    void joinOrganization() {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");
        var user = ZsrUserTestUtils.zsrUser("spell");
        var invite = new OrganizationInvite(organization, user);

        when(inviteUseRepository.save(any(OrganizationInviteUse.class))).then(returnsFirstArg());
        when(memberRepository.save(any(OrganizationMember.class))).then(returnsFirstArg());
        when(memberRepository.existsByOrganizationAndUser(organization, user)).thenReturn(false);

        var member = assertDoesNotThrow(() -> organizationService.joinOrganization(invite, user));

        var captor = ArgumentCaptor.forClass(OrganizationInviteUse.class);
        verify(inviteUseRepository, times(1)).save(captor.capture());
        verify(memberRepository, times(1)).save(member);

        assertEquals(user, member.getUser());
        assertEquals(organization, member.getOrganization());
        assertEquals(user, captor.getValue().getUser());
        assertEquals(invite, captor.getValue().getInvite());
    }

    @Test
    void joinOrganization_whenExpired_throwsInvalidInviteException() {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");
        var user = ZsrUserTestUtils.zsrUser("spell");
        var invite = new OrganizationInvite(organization, user);
        invite.setExpiresAt(OffsetDateTime.now().minus(7, ChronoUnit.DAYS));

        assertThrows(
                InvalidInviteException.class,
                () -> organizationService.joinOrganization(invite, user)
        );

        verify(inviteUseRepository, never()).save(any(OrganizationInviteUse.class));
        verify(memberRepository, never()).save(any(OrganizationMember.class));
    }

    @Test
    void joinOrganization_whenMaxUses_throwsInvalidInviteException() {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");
        var user = ZsrUserTestUtils.zsrUser("spell");
        var invite = new OrganizationInvite(organization, user);
        invite.setMaxUses(10L);

        when(inviteUseRepository.countByInvite(invite)).thenReturn(10L);

        assertThrows(
                InvalidInviteException.class,
                () -> organizationService.joinOrganization(invite, user)
        );

        verify(inviteUseRepository, never()).save(any(OrganizationInviteUse.class));
        verify(memberRepository, never()).save(any(OrganizationMember.class));
    }

    @Test
    void joinOrganization_whenInvalidated_throwsInvalidInviteException() {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");
        var user = ZsrUserTestUtils.zsrUser("spell");
        var invite = new OrganizationInvite(organization, user);
        invite.setInvalidated(true);

        assertThrows(
                InvalidInviteException.class,
                () -> organizationService.joinOrganization(invite, user)
        );

        verify(inviteUseRepository, never()).save(any(OrganizationInviteUse.class));
        verify(memberRepository, never()).save(any(OrganizationMember.class));
    }

    @Test
    void joinOrganization_whenAlreadyMember_throwsMembershipExistsException() {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");
        var user = ZsrUserTestUtils.zsrUser("spell");
        var invite = new OrganizationInvite(organization, user);

        when(memberRepository.existsByOrganizationAndUser(organization, user)).thenReturn(true);

        assertThrows(
                MembershipExistsException.class,
                () -> organizationService.joinOrganization(invite, user)
        );

        verify(inviteUseRepository, never()).save(any(OrganizationInviteUse.class));
        verify(memberRepository, never()).save(any(OrganizationMember.class));
    }
}