package com.zeldaspeedruns.zeldaspeedruns.security.user;

import com.zeldaspeedruns.zeldaspeedruns.security.SecureTokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class ZsrUserServiceImpl implements ZsrUserService {
    private final static Logger logger = LoggerFactory.getLogger(ZsrUserServiceImpl.class);

    private final ZsrUserRepository userRepository;
    private final RecoveryTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public ZsrUserServiceImpl(ZsrUserRepository userRepository,
                              RecoveryTokenRepository tokenRepository,
                              PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private RecoveryToken generateRecoveryToken(ZsrUser user) {
        String tokenValue = SecureTokenUtils.generateAlphanumericToken(RecoveryToken.TOKEN_VALUE_LENGTH);
        return tokenRepository.save(new RecoveryToken(user, tokenValue));
    }

    @Override
    public ZsrUser createUser(String username, String emailAddress, String password) throws UsernameInUseException, EmailInUseException {
        if (userRepository.existsByUsername(username)) {
            throw new UsernameInUseException(String.format("user with username '%s' already exists", username));
        }

        if (userRepository.existsByEmailAddress(emailAddress)) {
            throw new EmailInUseException(String.format("user with email address '%s' already exists", emailAddress));
        }

        password = passwordEncoder.encode(password);
        return userRepository.save(new ZsrUser(username, emailAddress, password));
    }

    @Override
    public ZsrUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("no user with username '%s' found", username)));
    }

    @Override
    public void changePassword(ZsrUser user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void startAccountRecovery(String emailAddress) {
        var userOptional = userRepository.findByEmailAddress(emailAddress);

        if (userOptional.isPresent()) {
            var user = userOptional.get();
            var token = generateRecoveryToken(user);
            logger.debug("Generated account recovery token {} for {}", token.getTokenValue(), user);
        }
    }

    @Override
    public boolean validateAccountRecoveryToken(String tokenValue) {
        var tokenOptional = tokenRepository.findByTokenValue(tokenValue);
        if (tokenOptional.isEmpty()) {
            return false;
        }

        return tokenOptional.get().isValid(Instant.now());
    }

    @Override
    @Transactional
    public void resetAccountPassword(String password, String tokenValue) throws TokenInvalidException {
        var tokenOptional = tokenRepository.findByTokenValue(tokenValue);
        if (tokenOptional.isEmpty()) {
            throw new TokenInvalidException("token value is invalid");
        }

        var token = tokenOptional.get();
        if (!token.isValid(Instant.now())) {
            throw new TokenInvalidException("token is invalid or has expired");
        }

        changePassword(token.getUser(), password);
        token.setConsumed(true);
        token.setConsumedAt(Instant.now());
        tokenRepository.save(token);
    }
}
