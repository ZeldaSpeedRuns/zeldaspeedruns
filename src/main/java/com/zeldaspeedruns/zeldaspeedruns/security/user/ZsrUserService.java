package com.zeldaspeedruns.zeldaspeedruns.security.user;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface ZsrUserService {
    /**
     * Creates a new user.
     * @param username Username for the user.
     * @param emailAddress Email address for the user.
     * @param password Unencrypted, plain password for the user.
     * @return Newly instantiated and saved user.
     * @throws UsernameInUseException Thrown if the username is already in use by another user.
     * @throws EmailInUseException Thrown if the email address is already in use by another user.
     */
    ZsrUser createUser(String username, String emailAddress, String password) throws UsernameInUseException, EmailInUseException;

    ZsrUser loadUserByUsername(String username) throws UsernameNotFoundException;

    /**
     * Sets the user's password to the new password.
     * @param user The user to change the password for.
     * @param password The desired new password in plaintext.
     */
    void changePassword(ZsrUser user, String password);

    /**
     * Starts the account recovery process for recovering lost credentials.
     * @param emailAddress The email address of the user.
     */
    void startAccountRecovery(String emailAddress);

    /**
     * Validates an account recovery token.
     * @param token The value of the token.
     * @return True if the token is valid, False if not.
     */
    boolean validateAccountRecoveryToken(String token);

    /**
     * Validates the token and resets the password for the user that the token belongs to.
     * @param password The new password to set for the user.
     * @param tokenValue The token to use.
     */
    void resetAccountPassword(String password, String tokenValue) throws TokenInvalidException;
}
