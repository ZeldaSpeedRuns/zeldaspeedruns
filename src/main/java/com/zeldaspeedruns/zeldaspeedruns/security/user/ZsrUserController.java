package com.zeldaspeedruns.zeldaspeedruns.security.user;

import com.zeldaspeedruns.zeldaspeedruns.security.user.forms.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping("/user")
public class ZsrUserController {
    private final ZsrUserService userService;

    public ZsrUserController(ZsrUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm() {
        return "user/login_form";
    }

    @GetMapping("/logout")
    public String logoutForm() {
        return "user/logout_form";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("form") RegistrationForm form) {
        return "user/register_form";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("form") RegistrationForm form,
                               BindingResult bindingResult) {
        try {
            if (!bindingResult.hasErrors()) {
                userService.createUser(form.getUsername(), form.getEmailAddress(), form.getPassword());
            }
        } catch (EmailInUseException | UsernameInUseException e) {
            bindingResult.reject("user.register.errors.username-email-taken", e.getMessage());
        }

        if (bindingResult.hasErrors()) {
            return "user/register_form";
        } else {
            return "user/register_success";
        }
    }

    @GetMapping("/recover-account")
    public String recoverAccountForm(@ModelAttribute("form") RecoverAccountForm form) {
        return "user/recover_account_form";
    }

    @PostMapping("/recover-account")
    public String recoverAccount(@Valid @ModelAttribute("form") RecoverAccountForm form,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "user/recover_account_form";
        }

        userService.startAccountRecovery(form.getEmailAddress());
        return "user/recover_account_success";
    }

    @GetMapping("/recover-account/{token}")
    public String resetPasswordForm(@PathVariable String token,
                                    @ModelAttribute("form") ResetPasswordForm form) {
        if (!userService.validateAccountRecoveryToken(token)) {
            return "redirect:/user/recover-account";
        }

        return "user/reset_password_form";
    }

    @PostMapping("/recover-account/{token}")
    public String resetPassword(@PathVariable String token,
                                @Valid @ModelAttribute("form") ResetPasswordForm form,
                                BindingResult bindingResult) throws Exception {
        if (bindingResult.hasErrors()) {
            return "user/reset_password_form";
        }

        userService.resetAccountPassword(form.getPassword(), token);
        return "redirect:/user/login";
    }
}
