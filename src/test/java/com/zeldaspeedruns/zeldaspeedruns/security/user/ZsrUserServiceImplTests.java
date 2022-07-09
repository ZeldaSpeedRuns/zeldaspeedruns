package com.zeldaspeedruns.zeldaspeedruns.security.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.AdditionalAnswers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = ZsrUserServiceImpl.class)
public class ZsrUserServiceImplTests {
    @MockBean
    private ZsrUserRepository repository;

    @MockBean
    private RecoveryTokenRepository tokenRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ZsrUserServiceImpl service;

    private ZsrUser user;

    @BeforeEach
    void beforeEach() {
        this.user = new ZsrUser("tester", "test@example.com", "password");
    }

    @Test
    void createUser_userIsSaved() throws Exception {
        when(repository.save(any(ZsrUser.class))).then(returnsFirstArg());
        when(passwordEncoder.encode(anyString())).then(returnsFirstArg());
        var createdUser = service.createUser("tester", "test@example.com", "password");

        verify(repository, times(1)).save(createdUser);
    }

    @Test
    void createUser_whenUsernameInUse_throwsException() {
        when(repository.existsByUsername(anyString())).thenReturn(true);

        assertThrows(UsernameInUseException.class, () -> {
            service.createUser("tester", "test@example.com", "password");
        });
    }

    @Test
    void createUser_whenEmailAddressInUse_throwsException() {
        when(repository.existsByEmailAddress(anyString())).thenReturn(true);

        assertThrows(EmailInUseException.class, () -> {
            service.createUser("tester", "test@example.com", "password");
        });
    }

    @Test
    void loadUserByUsername_whenFound_returnsUser() {
        when(repository.findByUsername(anyString())).thenAnswer(invocation -> {
            String username = invocation.getArgument(0);
            return Optional.of(ZsrUserTestUtils.zsrUser(username));
        });

        var user = assertDoesNotThrow(() -> service.loadUserByUsername("tester"));
        assertEquals("tester", user.getUsername());
    }

    @Test
    void loadUserByUsername_whenNotFound_throwsUsernameNotFoundException() {
        when(repository.findByUsername(anyString())).thenThrow(new UsernameNotFoundException("username"));
        assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername("username"));
    }

    @Test
    void changePassword_passwordIsEncoded() {
        when(repository.save(any(ZsrUser.class))).then(returnsFirstArg());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded_password");
        service.changePassword(user, "new_password");

        assertEquals(user.getPassword(), "encoded_password");
        verify(repository, times(1)).save(user);
    }

    @Test
    void startAccountRecovery_tokenIsSaved() {
        when(repository.findByEmailAddress(anyString())).thenReturn(Optional.of(user));
        when(tokenRepository.save(any(RecoveryToken.class))).then(returnsFirstArg());
        service.startAccountRecovery("test@example.com");

        verify(tokenRepository, times(1)).save(any(RecoveryToken.class));
    }

    @Test
    void startAccountRecovery_whenEmailAddressNotFound_noTokenIsSaved() {
        when(repository.findByEmailAddress(anyString())).thenReturn(Optional.empty());
        when(tokenRepository.save(any(RecoveryToken.class))).then(returnsFirstArg());
        service.startAccountRecovery("no-exist@example.com");

        verify(tokenRepository, times(0)).save(any(RecoveryToken.class));
    }

    @Test
    void validateAccountRecoveryToken_whenValid_returnsTrue() {
        when(tokenRepository.findByTokenValue(anyString())).thenAnswer(invocation -> {
            String tokenValue = invocation.getArgument(0);
            return Optional.of(new RecoveryToken(user, tokenValue));
        });

        assertTrue(service.validateAccountRecoveryToken("token-value"));
    }

    @Test
    void validateAccountRecoveryToken_whenInvalid_returnsFalse() {
        when(tokenRepository.findByTokenValue(anyString())).thenAnswer(invocation -> {
            String tokenValue = invocation.getArgument(0);
            RecoveryToken token = new RecoveryToken(user, tokenValue);
            token.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
            token.setConsumed(true);
            token.setConsumedAt(Instant.now().minus(3, ChronoUnit.HALF_DAYS));
            return Optional.of(token);
        });

        assertFalse(service.validateAccountRecoveryToken("token-value"));
    }

    @Test
    void validateAccountRecoveryToken_whenNotFound_returnsFalse() {
        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.empty());
        assertFalse(service.validateAccountRecoveryToken("token-value"));
    }

    @Test
    public void resetAccountPassword_setsNewPassword() {
        var token = new RecoveryToken(user, "token-value");
        var password = "password";
        var expectedPassword = "encoded-password";

        when(tokenRepository.findByTokenValue("token-value")).thenReturn(Optional.of(token));
        when(passwordEncoder.encode(anyString())).thenAnswer(invocation -> {
            String p = invocation.getArgument(0);
            return "encoded-" + p;
        });

        assertDoesNotThrow(() -> service.resetAccountPassword(password, "token-value"));
        assertEquals(expectedPassword, user.getPassword());
        assertTrue(token.isConsumed());
        assertTrue(token.getConsumedAt().isPresent());
        verify(repository, times(1)).save(user);
    }

    @Test
    public void resetAccountPassword_whenTokenNotFound_throwsException() {
        when(tokenRepository.findByTokenValue(anyString())).thenReturn(Optional.empty());
        var e = assertThrows(TokenInvalidException.class, () -> {
            service.resetAccountPassword("test", "token");
        });

        assertEquals("token value is invalid", e.getMessage());
    }

    @Test
    public void resetAccountPassword_whenTokenInvalid_throwsException() {
        when(tokenRepository.findByTokenValue(anyString())).thenAnswer(invocation -> {
            String tokenValue = invocation.getArgument(0);
            RecoveryToken token = new RecoveryToken(user, tokenValue);
            token.setExpiresAt(Instant.now().minus(1, ChronoUnit.DAYS));
            return Optional.of(token);
        });

        var e = assertThrows(TokenInvalidException.class, () -> {
            service.resetAccountPassword("test", "token");
        });

        assertEquals("token is invalid or has expired", e.getMessage());
    }
}
