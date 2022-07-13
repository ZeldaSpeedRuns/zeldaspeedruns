package com.zeldaspeedruns.zeldaspeedruns.security.user;

import com.zeldaspeedruns.zeldaspeedruns.security.user.forms.ChangePasswordForm;
import com.zeldaspeedruns.zeldaspeedruns.security.user.forms.RecoverAccountForm;
import com.zeldaspeedruns.zeldaspeedruns.security.user.forms.RegisterUserForm;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class ZsrUserController {
    private final ZsrUserService userService;

    public ZsrUserController(ZsrUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String getLoginForm() {
        return "user/login_form";
    }

    @GetMapping("/logout")
    public String getLogoutForm() {
        return "user/logout_form";
    }

    @GetMapping("/register")
    public String getRegisterForm(@ModelAttribute("form") RegisterUserForm form) {
        return "user/register_form";
    }

    @PostMapping("/register")
    public String postRegisterForm(@ModelAttribute("form") @Valid RegisterUserForm form,
                                   BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            try {
                userService.registerUser(form.getUsername(), form.getEmailAddress(), form.getPassword());
                return "user/register_success";
            } catch (EmailInUseException e) {
                bindingResult.reject("user.register.error.email-in-use", e.getMessage());
            } catch (MessagingException e) {
                bindingResult.reject("error.server-error", e.getMessage());
            } catch (UsernameInUseException e) {
                bindingResult.reject("user.register.error.username-in-use", e.getMessage());
            }
        }

        return "user/register_form";
    }

    @GetMapping("/confirm-email")
    public String confirmRegistration(@RequestParam String token) throws Exception {
        userService.confirmRegistration(token);
        return "user/confirm_email_success";
    }

    @GetMapping("/recover-account")
    public String getAccountRecoveryForm(@ModelAttribute("form") RecoverAccountForm form) {
        return "user/account_recovery_form";
    }

    @PostMapping("/recover-account")
    public String postAccountRecoveryForm(@ModelAttribute("form") @Valid RecoverAccountForm form,
                                          BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            try {
                userService.startAccountRecovery(form.getEmailAddress());
                return "user/account_recovery_success";
            } catch (MessagingException e) {
                bindingResult.reject("error.server-error", e.getMessage());
            }
        }

        return "user/account_recovery_form";
    }

    @GetMapping("/reset-password")
    public String getPasswordResetForm(@RequestParam String token,
                                       @ModelAttribute("form") ChangePasswordForm form) {
        if (!userService.tokenIsValid(token, ActionType.RECOVER_ACCOUNT)) {
            return "redirect:/user/recover-account";
        }

        return "user/reset_password_form";
    }

    @PostMapping("/reset-password")
    public String postPasswordResetForm(@RequestParam String token,
                                        @ModelAttribute("form") @Valid ChangePasswordForm form,
                                        BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            return "user/reset_password_form";
        }

        if (!userService.tokenIsValid(token, ActionType.RECOVER_ACCOUNT)) {
            return "redirect:/user/recover-account";
        }

        userService.resetPassword(form.getPassword(), token);
        return "user/reset_password_success";
    }

    @ResponseStatus(HttpStatus.GONE)
    @ExceptionHandler(ExpiredTokenException.class)
    public void expiredTokenExceptionHandler() {
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InvalidTokenException.class)
    public void invalidTokenExceptionHandler() {
    }
}
