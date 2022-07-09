package com.zeldaspeedruns.zeldaspeedruns.organization;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserRepository;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserTestUtils;
import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static com.zeldaspeedruns.zeldaspeedruns.organization.OrganizationController.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class OrganizationIntegrationTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private ZsrUserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private Organization organization;
    private ZsrUser user;

    @BeforeEach
    void beforeEach() {
        user = userRepository.save(ZsrUserTestUtils.zsrUser("spell"));
        organization = organizationRepository.save(new Organization("ZeldaSpeedRuns", "zsr"));
    }

    @AfterEach
    void afterEach() {
        organizationRepository.delete(organization);
        userRepository.delete(user);
    }

    @Test
    void createOrganization_whenAdmin_createsOrganization() throws Exception {
        ZsrUser user = ZsrUserTestUtils.zsrUser("spell");
        user.setAdministrator(true);

        String name = "The Silver Gauntlets";
        String slug = "silver-gauntlets";

        mvc.perform(post(BASE_URL + CREATE_ORGANIZATION_URL)
                        .with(user(new ZsrUserDetails(user)))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", name)
                        .param("slug", slug)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        var organizationOptional = organizationRepository.findBySlug(slug);
        assertTrue(organizationOptional.isPresent());

        var organization = organizationOptional.get();
        assertEquals(name, organization.getName());
        assertEquals(slug, organization.getSlug());
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteOrganization_whenAdmin_deletesOrganization() throws Exception {
        var slug = organization.getSlug();
        mvc.perform(post(BASE_URL + DELETE_ORGANIZATION_URL, slug)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(BASE_URL));

        var organizationOptional = organizationRepository.findBySlug(slug);
        assertFalse(organizationOptional.isPresent());
    }

    /**
     * Tests whether a user who has the WRITE permission set for an Organization ACL can edit the Organization.
     */
    @Test
    @WithMockUser(roles = "ADMIN")
    void editOrganization_whenUserHasWritePermission_canEdit() throws Exception {
        organizationService.grantOrganizationPermission(organization, user, BasePermission.WRITE);

        String description = "Welcome to ZeldaSpeedRuns.";

        mvc.perform(post(BASE_URL + EDIT_ORGANIZATION_URL, organization.getSlug())
                        .with(user(new ZsrUserDetails(user)))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("description", description)
                        .param("name", organization.getName())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        var o = organizationService.getBySlug(organization.getSlug());
        assertTrue(o.getDescription().isPresent());
        assertEquals(description, o.getDescription().get());
    }
}
