package com.zeldaspeedruns.zeldaspeedruns.security.user;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.ISpringTemplateEngine;

import java.util.Locale;

@Service
public class UserMailServiceImpl implements UserMailService {
    private final JavaMailSender mailSender;
    private final ISpringTemplateEngine templateEngine;
    private final MessageSource messageSource;

    public UserMailServiceImpl(JavaMailSender mailSender,
                               ISpringTemplateEngine templateEngine,
                               MessageSource messageSource) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
    }

    private Locale resolveLocale() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            return request.getLocale();
        } else {
            return Locale.getDefault();
        }
    }

    protected void sendMail(String to, String subject, String body) throws MessagingException {
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(to);
        helper.setFrom("no-reply@zeldaspeedruns.com");
        helper.setSubject(subject);
        helper.setText(body, true);
        mailSender.send(message);
    }

    @Override
    public void sendRegistrationConfirmationMail(ZsrUser user, UserActionToken token) throws MessagingException {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("tokenValue", token.getTokenValue());
        String body = templateEngine.process("user/registration_mail", context);
        var subject = messageSource.getMessage("user.register.mail.subject", null, resolveLocale());
        sendMail(user.getEmailAddress(), subject, body);
    }

    @Override
    public void sendAccountRecoveryMail(ZsrUser user, UserActionToken token) throws MessagingException {
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("tokenValue", token.getTokenValue());
        String body = templateEngine.process("user/account_recovery_mail", context);
        var subject = messageSource.getMessage("user.account-recovery.mail.subject", null, resolveLocale());
        sendMail(user.getEmailAddress(), subject, body);
    }
}
