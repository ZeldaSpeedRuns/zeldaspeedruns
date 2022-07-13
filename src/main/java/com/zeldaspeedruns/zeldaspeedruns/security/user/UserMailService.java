package com.zeldaspeedruns.zeldaspeedruns.security.user;

import javax.mail.MessagingException;

public interface UserMailService {
    void sendRegistrationConfirmationMail(ZsrUser user, UserActionToken token) throws MessagingException;

    void sendAccountRecoveryMail(ZsrUser user, UserActionToken token) throws MessagingException;
}
