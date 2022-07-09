package com.zeldaspeedruns.zeldaspeedruns.organization;

import com.zeldaspeedruns.zeldaspeedruns.organization.forms.CreateOrganizationForm;
import com.zeldaspeedruns.zeldaspeedruns.organization.forms.EditOrganizationForm;
import com.zeldaspeedruns.zeldaspeedruns.security.SecurityConfig;
import com.zeldaspeedruns.zeldaspeedruns.security.acl.AclConfig;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserTestUtils;
import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.zeldaspeedruns.zeldaspeedruns.organization.OrganizationController.*;
import static com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserTestUtils.zsrUser;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = OrganizationController.class)
public class OrganizationControllerTests {
    @MockBean
    private OrganizationService organizationService;

    @Autowired
    private MockMvc mvc;

    @Test
    void getOrganizations_returnsViewAndModel() throws Exception {
        var organizations = new PageImpl<>(List.of(
                new Organization("ZeldaSpeedRuns", "zsr"),
                new Organization("The Silver Gauntlets", "tsg")));

        when(organizationService.findAll(any())).thenReturn(organizations);

        mvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(ORGANIZATION_LIST_VIEW_NAME))
                .andExpect(model().attribute("organizations", organizations));
    }

    @Test
    void getOrganization_whenFound_isSetOnModel() throws Exception {
        Organization organization = new Organization("ZeldaSpeedRuns", "zsr");

        when(organizationService.getBySlug(organization.getSlug())).thenReturn(organization);

        mvc.perform(get(BASE_URL + GET_ORGANIZATION_URL, organization.getSlug()))
                .andExpect(status().isOk())
                .andExpect(view().name(ORGANIZATION_DETAIL_VIEW_NAME))
                .andExpect(model().attribute("organization", organization));
    }

    @Test
    void getOrganization_whenNotFound_returns404() throws Exception {
        when(organizationService.getBySlug(any())).thenThrow(new OrganizationNotFoundException("no such organization exists"));
        mvc.perform(get(BASE_URL + GET_ORGANIZATION_URL, "non-exist"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithAnonymousUser
    void createOrganizationForm_whenAnonymous_isRedirected() throws Exception {
        mvc.perform(get(BASE_URL + CREATE_ORGANIZATION_URL))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser
    void createOrganizationForm_whenUser_isForbidden() throws Exception {
        mvc.perform(get(BASE_URL + CREATE_ORGANIZATION_URL))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createOrganizationForm_whenAdmin_returnsForm() throws Exception {
        CreateOrganizationForm form = new CreateOrganizationForm();

        mvc.perform(get(BASE_URL + CREATE_ORGANIZATION_URL))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_ORGANIZATION_FORM_VIEW_NAME))
                .andExpect(model().attribute("form", form));
    }

    @Test
    void createOrganization_whenValid_createsOrganization() throws Exception {
        when(organizationService.createOrganization(any(), anyString(), anyString())).thenAnswer(invocation -> {
            String name = invocation.getArgument(1);
            String slug = invocation.getArgument(2);
            return new Organization(name, slug);
        });

        String name = "The Silver Gauntlets";
        String slug = "silver-gauntlets";

        mvc.perform(post(BASE_URL + CREATE_ORGANIZATION_URL)
                        .with(user(new ZsrUserDetails(zsrUser("spell"))))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", name)
                        .param("slug", slug)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organizations/" + slug));

        verify(organizationService, times(1)).createOrganization(any(), eq(name), eq(slug));
    }

    @Test
    void createOrganization_whenInvalid_returnsFormWithErrors() throws Exception {
        mvc.perform(post(BASE_URL + CREATE_ORGANIZATION_URL)
                        .with(user(new ZsrUserDetails(zsrUser("spell"))))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "xxx")
                        .param("slug", "-invalid-slug-X@-")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(CREATE_ORGANIZATION_FORM_VIEW_NAME))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attributeHasFieldErrors("form", "name", "slug"));

        verifyNoInteractions(organizationService);
    }

    @Test
    void updateOrganizationForm_whenFound_returnsForm() throws Exception {
        var organization = new Organization("ZeldaSpeedRuns", "zsr");
        when(organizationService.getBySlug("zsr")).thenReturn(organization);

        var expectedForm = new EditOrganizationForm();
        expectedForm.setName(organization.getName());
        expectedForm.setDescription(organization.getDescription().orElse(null));

        mvc.perform(get(BASE_URL + EDIT_ORGANIZATION_URL, "zsr"))
                .andExpect(status().isOk())
                .andExpect(view().name(EDIT_ORGANIZATION_FORM_VIEW_NAME))
                .andExpect(model().attribute("form", expectedForm));

        verify(organizationService, times(1)).getBySlug("zsr");
    }

    @Test
    void updateOrganizationForm_whenNotFound_returns404() throws Exception {
        when(organizationService.getBySlug(anyString())).thenThrow(new OrganizationNotFoundException("not found"));

        mvc.perform(get(BASE_URL + EDIT_ORGANIZATION_URL, "not-found"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateOrganization_whenValid_updatesOrganization() throws Exception {
        Organization organization = new Organization("ZeldaSpeedRuns", "zsr");

        when(organizationService.getBySlug(organization.getSlug())).thenReturn(organization);
        when(organizationService.updateOrganization(any(Organization.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        var expectedName = "ZeldaSpeedRuns 2";
        var expectedDescription = "The best place to watch Zelda speed runs.";

        mvc.perform(post(BASE_URL + EDIT_ORGANIZATION_URL, "zsr")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", expectedName)
                        .param("description", expectedDescription)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organizations/zsr"));

        verify(organizationService, times(1)).getBySlug("zsr");
        verify(organizationService, times(1)).updateOrganization(organization);
    }

    @Test
    void updateOrganization_whenInvalid_returnsFormWithErrors() throws Exception {
        mvc.perform(post(BASE_URL + EDIT_ORGANIZATION_URL, "zsr")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "xx") // too short
                        .param("description", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(EDIT_ORGANIZATION_FORM_VIEW_NAME))
                .andExpect(model().attributeHasFieldErrors("form", "name"));
    }
}
