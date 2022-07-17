package com.zeldaspeedruns.zeldaspeedruns.security.user.forms;

import com.zeldaspeedruns.zeldaspeedruns.validation.constraints.MustMatch;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@MustMatch(
        field = "passwordConfirmation", matchedField = "password",
        message = "{user.validation.password-must-match}"
)
public class ChangePasswordForm {
    @NotEmpty
    @Size(min = 4, max = 60)
    private String password;

    @NotNull
    private String passwordConfirmation;
}
