package com.zeldaspeedruns.zeldaspeedruns.security.user;

import com.zeldaspeedruns.zeldaspeedruns.security.SecureTokenUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ZsrUserServiceImplTests {
    @Mock
    private ZsrUserRepository userRepository;

    @Mock
    private UserActionTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMailService mailService;

    @InjectMocks
    private ZsrUserServiceImpl userService;

    private final String username = "spell";
    private final String emailAddress = "spell@example.com";
    private final String password = "password";
    private final String encodedPassword = "encoded";

    private ZsrUser user;

    private UserActionToken createToken(ActionType actionType) {
        String tokenValue = SecureTokenUtils.generateAlphanumericToken(40);
        return new UserActionToken(user, actionType, tokenValue);
    }

    @BeforeEach
    void beforeEach() {
        user = new ZsrUser(username, emailAddress, password);
    }

    @Test
    void loadByUsername() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        var returned = assertDoesNotThrow(() -> userService.loadByUsername(username));
        assertEquals(returned, user);
    }

    @Test
    void loadByUsername_whenNotFound_throwsUsernameNotFoundException() {
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> userService.loadByUsername(username));
    }

    @Test
    void createUser() {
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(userRepository.existsByEmailAddress(emailAddress)).thenReturn(false);
        when(userRepository.save(any(ZsrUser.class))).then(returnsFirstArg());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        ZsrUser u = assertDoesNotThrow(() -> userService.createUser(username, emailAddress, password));

        assertNotNull(u);
        assertEquals(username, u.getUsername());
        assertEquals(emailAddress, u.getEmailAddress());
        assertEquals(encodedPassword, u.getPassword());

        verify(userRepository, times(1)).save(any(ZsrUser.class));
        verify(passwordEncoder, times(1)).encode(password);
    }

    @Test
    void createRegistrationToken() {
        when(tokenRepository.save(any(UserActionToken.class))).then(returnsFirstArg());

        var token = userService.createRegistrationToken(user);

        assertNotNull(token);
        assertEquals(user, token.getUser());
        assertEquals(ActionType.CONFIRM_EMAIL, token.getActionType());
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    void createUser_whenUsernameTaken_throwsUsernameInUseException() {
        when(userRepository.existsByUsername(username)).thenReturn(true);
        assertThrows(UsernameInUseException.class, () -> {
            userService.createUser(username, emailAddress, password);
        });

        verify(userRepository, never()).save(any(ZsrUser.class));
        verifyNoInteractions(mailService);
    }

    @Test
    void createUser_whenEmailTaken_throwsEmailInUseException() {
        when(userRepository.existsByEmailAddress(emailAddress)).thenReturn(true);
        assertThrows(EmailInUseException.class, () -> {
            userService.createUser(username, emailAddress, password);
        });

        verify(userRepository, never()).save(any(ZsrUser.class));
        verifyNoInteractions(mailService);
    }

    @Test
    void registerUser() throws Exception {
        when(userRepository.save(any(ZsrUser.class))).then(returnsFirstArg());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);
        when(tokenRepository.save(any(UserActionToken.class))).then(returnsFirstArg());

        var user = assertDoesNotThrow(() -> userService.registerUser(username, emailAddress, password));

        assertNotNull(user);
        assertFalse(user.isEnabled());
        verify(mailService, times(1)).sendRegistrationConfirmationMail(any(), any());
    }

    @Test
    void confirmRegistration() {
        var token = createToken(ActionType.CONFIRM_EMAIL);
        var tokenValue = token.getTokenValue();

        when(tokenRepository.findByTokenValue(tokenValue)).thenReturn(Optional.of(token));

        assertDoesNotThrow(() -> userService.confirmRegistration(tokenValue));
        assertTrue(token.isConsumed());
        assertTrue(user.isEnabled());
    }

    @Test
    void confirmRegistration_whenExpired_throwsExpiredTokenException() {
        var token = createToken(ActionType.CONFIRM_EMAIL);
        var tokenValue = token.getTokenValue();
        token.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));

        when(tokenRepository.findByTokenValue(tokenValue)).thenReturn(Optional.of(token));

        assertThrows(ExpiredTokenException.class, () -> userService.confirmRegistration(tokenValue));
    }

    @Test
    void confirmRegistration_whenExpired_throwsInvalidTokenException() {
        var token = createToken(ActionType.CONFIRM_EMAIL);
        var tokenValue = token.getTokenValue();
        token.setConsumed(true);

        when(tokenRepository.findByTokenValue(tokenValue)).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> userService.confirmRegistration(tokenValue));
    }

    @Test
    void confirmRegistration_invalidTokenType_throwsInvalidTokenException() {
        var token = createToken(ActionType.RECOVER_ACCOUNT);
        var tokenValue = token.getTokenValue();

        when(tokenRepository.findByTokenValue(tokenValue)).thenReturn(Optional.of(token));

        assertThrows(InvalidTokenException.class, () -> userService.confirmRegistration(tokenValue));
    }

    @Test
    void startAccountRecovery() throws Exception {
        when(userRepository.findByEmailAddress(emailAddress)).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(UserActionToken.class))).then(returnsFirstArg());

        userService.startAccountRecovery(emailAddress);

        verify(tokenRepository, times(1)).save(any(UserActionToken.class));
        verify(mailService, times(1)).sendAccountRecoveryMail(eq(user), any(UserActionToken.class));
    }

    @Test
    void startAccountRecovery_whenEmailNotFound_doNothing() throws Exception {
        when(userRepository.findByEmailAddress(emailAddress)).thenReturn(Optional.empty());

        userService.startAccountRecovery(emailAddress);

        verifyNoInteractions(tokenRepository);
        verifyNoInteractions(mailService);
    }

    @Test
    void resetPassword() throws Exception {
        var token = createToken(ActionType.RECOVER_ACCOUNT);
        var newPassword = "new-password";

        when(tokenRepository.findByTokenValue(token.getTokenValue())).thenReturn(Optional.of(token));
        when(passwordEncoder.encode(newPassword)).thenReturn("encoded-password");

        userService.resetPassword(newPassword, token.getTokenValue());
        assertTrue(token.isConsumed());
        assertEquals("encoded-password", user.getPassword());
    }
}
