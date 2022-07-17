package com.zeldaspeedruns.zeldaspeedruns.security.user.forms;

import com.zeldaspeedruns.zeldaspeedruns.validation.constraints.MustMatch;
import lombok.Data;

import jakarta.validation.constraints.*;

@Data
@MustMatch(
        field = "passwordConfirmation", matchedField = "password",
        message = "{user.validation.password-must-match}"
)
public class RegisterUserForm {
    @NotEmpty
    @Size(min = 3, max = 40)
    @Pattern(regexp = "^\\w+$", message = "{user.validation.username-pattern}")
    private String username;

    @Email
    @NotEmpty
    @Size(min = 4, max = 128)
    private String emailAddress;

    @NotEmpty
    @Size(min = 4, max = 60)
    private String password;

    @NotNull
    private String passwordConfirmation;
}
