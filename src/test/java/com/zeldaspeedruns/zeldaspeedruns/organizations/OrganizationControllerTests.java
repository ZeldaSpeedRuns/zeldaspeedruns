package com.zeldaspeedruns.zeldaspeedruns.organizations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Import(OrganizationControllerTestConfiguration.class)
@WebMvcTest(OrganizationController.class)
@TestPropertySource("classpath:application-testing.properties")
class OrganizationControllerTests {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private OrganizationService organizationService;

    @Test
    void getOrganizations() throws Exception {
        var organizations = List.of(
                OrganizationTestUtils.organization("ZeldaSpeedRuns"),
                OrganizationTestUtils.organization("The Silver Gauntlets")
        );

        when(organizationService.findAllOrganizations(any(Pageable.class)))
                .then(i -> new PageImpl<>(organizations, i.getArgument(0), organizations.size()));

        mvc.perform(get("/organizations"))
                .andExpect(status().isOk())
                .andExpect(view().name("organizations/list"))
                .andExpect(model().attributeExists("organizations"));
    }

    @Test
    void getOrganization() throws Exception {
        var organization = OrganizationTestUtils.organization("ZeldaSpeedRuns");

        when(organizationService.findOrganizationBySlug(organization.getSlug()))
                .thenReturn(Optional.of(organization));

        mvc.perform(get("/organizations/{slug}", organization.getSlug()))
                .andExpect(status().isOk())
                .andExpect(view().name("organizations/detail"))
                .andExpect(model().attribute("organization", organization));
    }

    @Test
    void getOrganization_whenNotFound_404() throws Exception {
        when(organizationService.findOrganizationBySlug(anyString()))
                .thenReturn(Optional.empty());

        mvc.perform(get("/organizations/{slug}", "not-found"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "spell", roles = {"USER", "ADMIN"})
    void getOrganizationCreationForm() throws Exception {
        mvc.perform(get("/organizations/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("organizations/create_form"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    @WithMockUser(username = "spell", roles = "USER")
    void getOrganizationCreationForm_whenNotAdmin_403() throws Exception {
        mvc.perform(get("/organizations/create"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getOrganizationsCreationForm_whenUnauthenticated_3xxRedirection() throws Exception {
        mvc.perform(get("/organizations/create"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "spell", roles = {"USER", "ADMIN"})
    void postOrganizationCreationForm() throws Exception {
        var name = "ZeldaSpeedRuns";
        var slug = "zeldaspeedruns";

        when(organizationService.createOrganization(name, slug))
                .thenReturn(OrganizationTestUtils.organization(name));

        mvc.perform(post("/organizations/create")
                        .param("name", name)
                        .param("slug", slug)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlTemplate("/organizations/{slug}", slug));

        verify(organizationService, atLeastOnce()).createOrganization(name, slug);
    }

    @Test
    @WithMockUser(username = "spell", roles = {"USER", "ADMIN"})
    void postOrganizationCreationForm_whenInvalid_hasErrors() throws Exception {
        mvc.perform(post("/organizations/create")
                        .param("name", "x")
                        .param("slug", "Invalid#slugXD-")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("organizations/create_form"))
                .andExpect(model().attributeHasFieldErrors("form", "name", "slug"));
    }

    @Test
    @WithMockUser(username = "spell", roles = "USER")
    void postOrganizationCreationForm_whenNotAdmin_403() throws Exception {
        mvc.perform(post("/organizations/create")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }

    @Test
    void postOrganizationsCreationForm_whenUnauthenticated_3xxRedirection() throws Exception {
        mvc.perform(post("/organizations/create")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }
}
