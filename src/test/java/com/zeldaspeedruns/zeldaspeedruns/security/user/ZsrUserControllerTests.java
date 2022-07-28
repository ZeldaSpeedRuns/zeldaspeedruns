package com.zeldaspeedruns.zeldaspeedruns.security.user;

import com.zeldaspeedruns.zeldaspeedruns.security.SecureTokenUtils;
import com.zeldaspeedruns.zeldaspeedruns.security.SecurityConfig;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ZsrUserController.class)
@Import(SecurityConfig.class)
@TestPropertySource("classpath:application-testing.properties")
class ZsrUserControllerTests {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private ZsrUserService userService;

    private MultiValueMap<String, String> validUserForm() {
        var params = new LinkedMultiValueMap<String, String>();
        params.add("username", "spell");
        params.add("emailAddress", "spell@example.com");
        params.add("password", "Rand0mV#lid!");
        params.add("passwordConfirmation", "Rand0mV#lid!");
        return params;
    }

    private ResultActions performValidRegistration() throws Exception {
        return mvc.perform(post("/user/register")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .params(validUserForm())
                .with(csrf()));
    }

    @Test
    void getLoginForm() throws Exception {
        mvc.perform(get("/user/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/login_form"));
    }

    @Test
    void getLogoutForm() throws Exception {
        mvc.perform(get("/user/logout"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/logout_form"));
    }

    @Test
    void getRegisterForm() throws Exception {
        mvc.perform(get("/user/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/register_form"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    void registerUser_whenValid_createsUser() throws Exception {
        performValidRegistration()
                .andExpect(status().isOk())
                .andExpect(view().name("user/register_success"))
                .andExpect(model().hasNoErrors());

        verify(userService, times(1)).registerUser("spell", "spell@example.com", "Rand0mV#lid!");
    }

    @Test
    void registerUser_whenInvalid_setsError() throws Exception {
        mvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("username", "invalid username!")
                        .param("emailAddress", "notvalid")
                        .param("password", "xx")
                        .param("passwordConfirmation", "")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/register_form"))
                .andExpect(model().attributeHasFieldErrors("form", "username", "emailAddress", "password", "passwordConfirmation"));

        verifyNoInteractions(userService);
    }

    @Test
    void registerUser_whenEmailFailed_setsError() throws Exception {
        when(userService.registerUser(anyString(), anyString(), anyString()))
                .thenThrow(new MessagingException("failed to send email"));

        performValidRegistration()
                .andExpect(status().isOk())
                .andExpect(view().name("user/register_form"))
                .andExpect(model().attributeHasErrors("form"));
    }

    @Test
    void registerUser_whenUsernameInUse_setsError() throws Exception {
        when(userService.registerUser(anyString(), anyString(), anyString()))
                .thenThrow(new UsernameInUseException("username in use"));

        performValidRegistration()
                .andExpect(status().isOk())
                .andExpect(view().name("user/register_form"))
                .andExpect(model().attributeHasErrors("form"));
    }

    @Test
    void registerUser_whenEmailInUse_setsError() throws Exception {
        when(userService.registerUser(anyString(), anyString(), anyString()))
                .thenThrow(new EmailInUseException("email in use"));

        performValidRegistration()
                .andExpect(status().isOk())
                .andExpect(view().name("user/register_form"))
                .andExpect(model().attributeHasErrors("form"));
    }

    @Test
    void confirmEmailAddress_whenValid_success() throws Exception {
        String tokenValue = SecureTokenUtils.generateAlphanumericToken(40);

        mvc.perform(get("/user/confirm-email")
                        .queryParam("token", tokenValue))
                .andExpect(status().isOk())
                .andExpect(view().name("user/confirm_email_success"));

        verify(userService, times(1)).confirmRegistration(tokenValue);
    }

    @Test
    void confirmEmailAddress_whenNoToken_returnsBadRequest() throws Exception {
        mvc.perform(get("/user/confirm-email"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void confirmEmailAddress_whenTokenInvalid_returnsBadRequest() throws Exception {
        doThrow(new InvalidTokenException("invalid token"))
                .when(userService)
                .confirmRegistration(anyString());

        mvc.perform(get("/user/confirm-email")
                        .queryParam("token", SecureTokenUtils.generateAlphanumericToken(40)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void confirmEmailAddress_whenTokenExpired_returnsGone() throws Exception {
        doThrow(new ExpiredTokenException("expired token"))
                .when(userService)
                .confirmRegistration(anyString());

        mvc.perform(get("/user/confirm-email")
                        .queryParam("token", SecureTokenUtils.generateAlphanumericToken(40)))
                .andExpect(status().isGone());
    }

    @Test
    void getAccountRecoveryForm() throws Exception {
        mvc.perform(get("/user/recover-account"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/account_recovery_form"))
                .andExpect(model().attributeExists("form"));
    }

    @Test
    void postAccountRecoveryForm() throws Exception {
        mvc.perform(post("/user/recover-account")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailAddress", "spell@example.com")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/account_recovery_success"))
                .andExpect(model().hasNoErrors());

        verify(userService, times(1)).startAccountRecovery("spell@example.com");
    }

    @Test
    void postAccountRecoveryForm_whenInvalid_setsError() throws Exception {
        mvc.perform(post("/user/recover-account")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailAddress", "invalid")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/account_recovery_form"))
                .andExpect(model().attributeHasFieldErrors("form", "emailAddress"));


        verifyNoInteractions(userService);
    }

    @Test
    void postAccountRecoveryForm_whenEmailFailed_setsError() throws Exception {
        doThrow(new MessagingException("email failed")).when(userService).startAccountRecovery(anyString());

        mvc.perform(post("/user/recover-account")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("emailAddress", "spell@example.com")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/account_recovery_form"))
                .andExpect(model().attributeHasErrors("form"));
    }

    @Test
    void getResetPasswordForm() throws Exception {
        var token = SecureTokenUtils.generateAlphanumericToken(40);

        when(userService.tokenIsValid(token, ActionType.RECOVER_ACCOUNT)).thenReturn(true);

        mvc.perform(get("/user/reset-password")
                        .queryParam("token", token))
                .andExpect(status().isOk())
                .andExpect(view().name("user/reset_password_form"))
                .andExpect(model().attributeExists("form"));

        verify(userService, times(1)).tokenIsValid(token, ActionType.RECOVER_ACCOUNT);
    }

    @Test
    void getResetPasswordForm_whenInvalidToken_redirects() throws Exception {
        when(userService.tokenIsValid(anyString(), eq(ActionType.RECOVER_ACCOUNT))).thenReturn(false);

        mvc.perform(get("/user/reset-password")
                        .queryParam("token", "invalid-token"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/recover-account"));

        verify(userService, times(1)).tokenIsValid(anyString(), eq(ActionType.RECOVER_ACCOUNT));
    }

    @Test
    void postResetPasswordForm() throws Exception {
        var token = SecureTokenUtils.generateAlphanumericToken(40);
        var password = "new-password";

        when(userService.tokenIsValid(token, ActionType.RECOVER_ACCOUNT)).thenReturn(true);

        mvc.perform(post("/user/reset-password")
                        .queryParam("token", token)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", password)
                        .param("passwordConfirmation", password)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/reset_password_success"))
                .andExpect(model().hasNoErrors());

        verify(userService, times(1)).tokenIsValid(token, ActionType.RECOVER_ACCOUNT);
        verify(userService, times(1)).resetPassword(password, token);
    }

    @Test
    void postResetPassword_whenInvalidToken_redirects() throws Exception {
        var password = "new-password";

        when(userService.tokenIsValid(anyString(), eq(ActionType.RECOVER_ACCOUNT))).thenReturn(false);

        mvc.perform(post("/user/reset-password")
                        .queryParam("token", "invalid-token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", password)
                        .param("passwordConfirmation", password)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/recover-account"));

        verify(userService, times(1)).tokenIsValid(anyString(), eq(ActionType.RECOVER_ACCOUNT));
        verify(userService, never()).resetPassword(anyString(), anyString());
    }

    @Test
    void postResetPassword_whenInvalidForm_setsError() throws Exception {
        mvc.perform(post("/user/reset-password")
                        .queryParam("token", "valid-token")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("password", "xd") // too short
                        .param("passwordConfirmation", "xxxxxx") // does not match
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("user/reset_password_form"))
                .andExpect(model().attributeHasFieldErrors("form", "password", "passwordConfirmation"));

        verifyNoInteractions(userService);
    }
}
