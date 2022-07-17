package com.zeldaspeedruns.zeldaspeedruns.security.user;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jakarta.mail.MessagingException;

public interface ZsrUserService {
    /**
     * Loads a user from the persistent storage.
     *
     * @param username The username of the user to find.
     * @return The user.
     * @throws UsernameNotFoundException Thrown when no user with the given username exists.
     */
    ZsrUser loadByUsername(String username) throws UsernameNotFoundException;

    ZsrUser loadByEmailAddress(String emailAddress) throws EmailNotFoundException;

    /**
     * Creates and saves a new user to the persistent storage.
     *
     * @param username     The username of the new user.
     * @param emailAddress The email address of the new user.
     * @param password     The unencrypted, plaintext password of the new user.
     * @return Fully instantiated new user.
     * @throws UsernameInUseException Thrown when a user with the given username already exists.
     * @throws EmailInUseException    Thrown when a user with the given email address already exists.
     * @see ZsrUserService#registerUser(String username, String emailAddress, String password)
     */
    ZsrUser createUser(String username, String emailAddress, String password) throws UsernameInUseException, EmailInUseException;

    /**
     * Registers a user, creating a new account, a registration confirmation token and mails it to the user's given
     * email address.
     *
     * @param username     The username of the new user.
     * @param emailAddress Valid email address of the new user.
     * @param password     Unencrypted, plaintext password.
     * @return The newly created user.
     * @throws UsernameInUseException Thrown when the username is already in use.
     * @throws EmailInUseException    Thrown when the email address is already in use.
     * @throws MessagingException     Thrown when the email failed to send successfully.
     */
    ZsrUser registerUser(String username, String emailAddress, String password) throws UsernameInUseException, EmailInUseException, MessagingException;

    /**
     * Attempts to confirm a registration for the given token. If the token is expired or already consumed, the
     * activation will fail. If successful, the user account attached to the token will be activated.
     *
     * @param tokenValue The token to look up for confirmation.
     * @throws InvalidTokenException Thrown when the token is invalid.
     * @throws ExpiredTokenException Thrown when the token has expired.
     */
    void confirmRegistration(String tokenValue) throws InvalidTokenException, ExpiredTokenException;

    /**
     * Starts the account recovery process, sending an account recovery token to the user that can be used to reset the
     * password on the account.
     *
     * @param emailAddress The user's email address.
     * @throws MessagingException Thrown when the recovery message failed to send.
     */
    void startAccountRecovery(String emailAddress) throws MessagingException;

    /**
     * Resets the user's password without the need for authentication.
     *
     * @param password   The user's desired new password.
     * @param tokenValue Token value that validates this request.
     * @throws InvalidTokenException Thrown when the token is invalid.
     * @throws ExpiredTokenException Thrown when the token has expired.
     */
    void resetPassword(String password, String tokenValue) throws InvalidTokenException, ExpiredTokenException;

    /**
     * Checks whether a given token is valid for the action type.
     *
     * @param tokenValue The value of the token to check.
     * @param actionType The type of action the token should be valid for.
     * @return True if the token is valid, False if not.
     */
    boolean tokenIsValid(String tokenValue, ActionType actionType);
}
