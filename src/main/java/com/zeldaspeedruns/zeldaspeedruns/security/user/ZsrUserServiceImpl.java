package com.zeldaspeedruns.zeldaspeedruns.security.user;

import com.zeldaspeedruns.zeldaspeedruns.security.SecureTokenUtils;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Implements the ZsrUserService interface using Spring Data repositories.
 */
@Service
public class ZsrUserServiceImpl implements ZsrUserService {
    private final static Logger logger = LoggerFactory.getLogger(ZsrUserServiceImpl.class);

    private final ZsrUserRepository userRepository;
    private final UserActionTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMailService mailService;

    public ZsrUserServiceImpl(ZsrUserRepository userRepository,
                              UserActionTokenRepository tokenRepository,
                              PasswordEncoder passwordEncoder,
                              UserMailService mailService) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailService = mailService;
    }

    /**
     * Loads a token from the database.
     *
     * @param tokenValue The alphanumeric string value of the token.
     * @param actionType The type of the token.
     * @return Instantiated UserActionToken entity.
     * @throws InvalidTokenException Thrown if the token is invalid.
     * @throws ExpiredTokenException Thrown if the token has expired.
     */
    protected UserActionToken loadToken(String tokenValue, ActionType actionType) throws InvalidTokenException, ExpiredTokenException {
        var tokenOptional = tokenRepository.findByTokenValue(tokenValue);

        if (tokenOptional.isPresent()) {
            var token = tokenOptional.get();
            if (token.hasExpired(Instant.now())) {
                throw new ExpiredTokenException("the token has expired");
            }
            if (token.isConsumed()) {
                throw new InvalidTokenException("the token has been consumed");
            }
            if (!token.getActionType().equals(actionType)) {
                throw new InvalidTokenException("invalid token type");
            }
            return token;
        } else {
            throw new InvalidTokenException("invalid token value");
        }
    }

    @Override
    public ZsrUser loadByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("no user with username '%s' exists", username)));
    }

    @Override
    public ZsrUser loadByEmailAddress(String emailAddress) throws EmailNotFoundException {
        return userRepository
                .findByEmailAddressIgnoreCase(emailAddress)
                .orElseThrow(() -> new EmailNotFoundException(String.format("no user with email address '%s' exists", emailAddress)));
    }

    @Override
    @Transactional
    public ZsrUser createUser(String username, String emailAddress, String password) throws UsernameInUseException, EmailInUseException {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new UsernameInUseException(String.format("user with username '%s' already exists", username));
        }

        if (userRepository.existsByEmailAddressIgnoreCase(emailAddress)) {
            throw new EmailInUseException(String.format("user with email address '%s' already exists", emailAddress));
        }

        String encodedPassword = passwordEncoder.encode(password);
        return userRepository.save(new ZsrUser(username, emailAddress, encodedPassword));
    }

    protected UserActionToken createRegistrationToken(ZsrUser user) {
        String tokenValue = SecureTokenUtils.generateAlphanumericToken(UserActionToken.TOKEN_VALUE_LENGTH);
        var token = new UserActionToken(user, ActionType.CONFIRM_EMAIL, tokenValue);
        token = tokenRepository.save(token);
        logger.debug("Generated registration token with value {} for user {}", token.getTokenValue(), user.getUsername());
        return token;
    }

    @Override
    @Transactional
    public ZsrUser registerUser(String username, String emailAddress, String password) throws UsernameInUseException, EmailInUseException, MessagingException {
        ZsrUser user = createUser(username, emailAddress, password);
        user.setEnabled(false);

        try {
            UserActionToken token = createRegistrationToken(user);
            mailService.sendRegistrationConfirmationMail(user, token);
        } catch (InvalidTokenException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    @Override
    @Transactional
    public void confirmRegistration(String tokenValue) throws InvalidTokenException, ExpiredTokenException {
        var token = loadToken(tokenValue, ActionType.CONFIRM_EMAIL);
        token.getUser().setEnabled(true);
        token.consume();
        logger.debug("Activated user {} with token {}", token.getUser().getUsername(), tokenValue);
    }

    @Override
    public void startAccountRecovery(String emailAddress) throws MessagingException {
        var userOptional = userRepository.findByEmailAddressIgnoreCase(emailAddress);

        if (userOptional.isPresent()) {
            var user = userOptional.get();

            try {
                var tokenValue = SecureTokenUtils.generateAlphanumericToken(40);
                var token = new UserActionToken(user, ActionType.RECOVER_ACCOUNT, tokenValue);
                token = tokenRepository.save(token);
                logger.debug("Generated account recovery token {} for user {}", tokenValue, user.getUsername());

                mailService.sendAccountRecoveryMail(user, token);
            } catch (InvalidTokenException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    @Transactional
    public void resetPassword(String password, String tokenValue) throws InvalidTokenException, ExpiredTokenException {
        var token = loadToken(tokenValue, ActionType.RECOVER_ACCOUNT);
        token.getUser().setPassword(passwordEncoder.encode(password));
        token.consume();
        logger.debug("Reset password for user {} using token {}", token.getUser().getUsername(), tokenValue);
    }

    @Override
    public boolean tokenIsValid(String tokenValue, ActionType actionType) {
        try {
            loadToken(tokenValue, actionType);
            return true;
        } catch (InvalidTokenException | ExpiredTokenException ignored) {
            return false;
        }
    }
}
