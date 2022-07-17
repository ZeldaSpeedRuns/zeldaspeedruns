package com.zeldaspeedruns.zeldaspeedruns.security.user;

import jakarta.mail.MessagingException;

public interface UserMailService {
    void sendRegistrationConfirmationMail(ZsrUser user, UserActionToken token) throws MessagingException;

    void sendAccountRecoveryMail(ZsrUser user, UserActionToken token) throws MessagingException;
}
