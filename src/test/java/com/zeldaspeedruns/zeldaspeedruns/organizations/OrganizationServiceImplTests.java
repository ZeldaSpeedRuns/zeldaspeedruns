package com.zeldaspeedruns.zeldaspeedruns.organizations;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
}