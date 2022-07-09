package com.zeldaspeedruns.zeldaspeedruns.security.user;

import com.zeldaspeedruns.zeldaspeedruns.security.SecureTokenUtils;
import com.zeldaspeedruns.zeldaspeedruns.security.SecurityConfig;
import com.zeldaspeedruns.zeldaspeedruns.security.acl.AclConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserTestUtils.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Import(SecurityConfig.class)
@WebMvcTest(controllers = ZsrUserController.class)
public class ZsrUserControllerIntegrationTests {
    @MockBean
    private ZsrUserService userService;

    @Autowired
    private MockMvc mvc;

    private final String username = "username";
    private final String emailAddress = "username@example.com";
    private final String password = "password";
    private final String token = SecureTokenUtils.generateAlphanumericToken(20);

    private MultiValueMap<String, String> validRegistrationForm() {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("username", username);
        form.add("emailAddress", emailAddress);
        form.add("emailAddressConfirmation", emailAddress);
        form.add("password", password);
        form.add("passwordConfirmation", password);
        return form;
    }

    @Test
    void registerForm_whenUnauthenticated_returns200() throws Exception {
        mvc.perform(get("/user/register").with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/register_form"));
    }

    @Test
    void registerForm_whenAuthenticated_returns4xx() throws Exception {
        mvc.perform(get("/user/register").with(user(zsrUserDetails("spell"))))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void registerUser_whenValid_createsUserAndReturns200() throws Exception {
        mvc.perform(post("/user/register")
                        .with(anonymous())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(validRegistrationForm())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/register_success"));

        verify(userService, times(1)).createUser(username, emailAddress, password);
    }

    @Test
    void registerUser_whenAuthenticated_returns4xx() throws Exception {
        mvc.perform(post("/user/register").with(user(zsrUserDetails("spell"))))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void registerUser_whenInvalid_hasErrors() throws Exception {
        mvc.perform(post("/user/register")
                        .with(anonymous())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "")
                        .param("emailAddress", "invalidmail")
                        .param("emailAddressConfirmation", "doesnotmatch@example.com")
                        .param("password", "x")
                        .param("passwordConfirmation", "x")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/register_form"))
                .andExpect(model().attributeHasErrors("form"))
                .andExpect(model().attributeHasFieldErrors("form",
                        "username", "emailAddress", "emailAddressConfirmation", "password"));

        verifyNoInteractions(userService);
    }

    @Test
    void registerUser_whenUsernameInUse_hasFormError() throws Exception {
        when(userService.createUser(anyString(), anyString(), anyString()))
                .thenThrow(new UsernameInUseException("username is in use"));

        mvc.perform(post("/user/register")
                        .with(anonymous())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(validRegistrationForm())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/register_form"))
                .andExpect(model().attributeHasErrors("form"));

        verify(userService, atLeastOnce()).createUser(anyString(), anyString(), anyString());
    }

    @Test
    void registerUser_whenEmailInUse_hasFormError() throws Exception {
        when(userService.createUser(anyString(), anyString(), anyString()))
                .thenThrow(new EmailInUseException("email address is in use"));

        mvc.perform(post("/user/register")
                        .with(anonymous())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .params(validRegistrationForm())
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/register_form"))
                .andExpect(model().attributeHasErrors("form"));

        verify(userService, atLeastOnce()).createUser(anyString(), anyString(), anyString());
    }

    @Test
    void recoverAccountForm_whenUnauthenticated_returns200() throws Exception {
        mvc.perform(get("/user/recover-account").with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/recover_account_form"));
    }

    @Test
    void recoverAccountForm_whenAuthenticated_returns4xx() throws Exception {
        mvc.perform(get("/user/recover-account").with(user(zsrUserDetails("spell"))))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void recoverAccount_whenValid_returns200() throws Exception {
        mvc.perform(post("/user/recover-account")
                        .with(anonymous())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailAddress", "user@example.com")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/recover_account_success"));

        verify(userService, times(1)).startAccountRecovery("user@example.com");
    }

    @Test
    void recoverAccount_whenInvalid_formHasErrors() throws Exception {
        mvc.perform(post("/user/recover-account")
                        .with(anonymous())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailAddress", "invalidemail")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/recover_account_form"))
                .andExpect(model().attributeHasFieldErrors("form", "emailAddress"));

        verifyNoInteractions(userService);
    }

    @Test
    void recoverAccount_whenAuthenticated_returns4xx() throws Exception {
        mvc.perform(post("/user/recover-account")
                        .with(user(zsrUserDetails("spell")))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailAddress", "user@example.com")
                        .with(csrf()))
                .andExpect(status().is4xxClientError());

        verifyNoInteractions(userService);
    }

    @Test
    void resetPasswordForm_whenUnauthenticated_returns200() throws Exception {
        when(userService.validateAccountRecoveryToken(token)).thenReturn(true);

        mvc.perform(get("/user/recover-account/{token}", token).with(anonymous()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/reset_password_form"));

        verify(userService, times(1)).validateAccountRecoveryToken(token);
    }

    @Test
    void resetPasswordForm_whenAuthenticated_returns4xx() throws Exception {
        mvc.perform(get("/user/recover-account/{token}", token)
                        .with(user(zsrUserDetails("spell"))))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void resetPasswordForm_whenTokenInvalid_redirectsToForm() throws Exception {
        when(userService.validateAccountRecoveryToken(token)).thenReturn(false);

        mvc.perform(get("/user/recover-account/{token}", token).with(anonymous()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/recover-account"));

        verify(userService, times(1)).validateAccountRecoveryToken(token);
    }

    @Test
    void resetPassword_whenValid_setsUserPassword() throws Exception {
        var newPassword = "new-password";

        mvc.perform(post("/user/recover-account/{token}", token)
                        .with(anonymous())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", newPassword)
                        .param("passwordConfirmation", newPassword)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/login"));

        verify(userService, times(1)).resetAccountPassword(newPassword, token);
    }

    @Test
    void resetPassword_whenAuthenticated_returns4xx() throws Exception {
        var newPassword = "new-password";

        mvc.perform(post("/user/recover-account/{token}", token)
                        .with(user(zsrUserDetails("spell")))
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", newPassword)
                        .param("passwordConfirmation", newPassword)
                        .with(csrf()))
                .andExpect(status().is4xxClientError());

        verifyNoInteractions(userService);
    }

    @Test
    void resetPassword_whenInvalid_hasFormError() throws Exception {
        mvc.perform(post("/user/recover-account/{token}", token)
                        .with(anonymous())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", "x")
                        .param("passwordConfirmation", "xxx")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/reset_password_form"))
                .andExpect(model().attributeHasErrors("form"));

        verifyNoInteractions(userService);
    }
}
