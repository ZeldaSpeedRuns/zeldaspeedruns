package com.zeldaspeedruns.zeldaspeedruns.security.user;

import jakarta.mail.MessagingException;

/**
 * The UserMailService provides functionality to send system messages to users.
 */
public interface UserMailService {
    /**
     * Sends a registration confirmation email to the user.
     *
     * @param user  The ZsrUser entity to send the email to.
     * @param token The UserActionToken to use, must be of CONFIRM_EMAIL type.
     * @throws MessagingException    Thrown if the email fails to send.
     * @throws InvalidTokenException Thrown if the token is of the wrong type.
     */
    void sendRegistrationConfirmationMail(ZsrUser user, UserActionToken token) throws MessagingException, InvalidTokenException;

    /**
     * Sends an account recovery email to the user.
     *
     * @param user  The ZsrUser entity to send the email to.
     * @param token The UserActionToken to use, must be of RECOVER_ACCOUNT type.
     * @throws MessagingException    Thrown if the email fails to send.
     * @throws InvalidTokenException Thrown if the token is of the wrong type.
     */
    void sendAccountRecoveryMail(ZsrUser user, UserActionToken token) throws MessagingException, InvalidTokenException;
}
