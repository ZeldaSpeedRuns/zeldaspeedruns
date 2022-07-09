package com.zeldaspeedruns.zeldaspeedruns.security.user.forms;

import com.zeldaspeedruns.zeldaspeedruns.validation.constraints.MustMatch;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

@Data
@MustMatch(field = "password", matchedField = "passwordConfirmation")
public class ResetPasswordForm {
    @Length(min = 3, max = 40)
    private String password;

    @NotEmpty
    private String passwordConfirmation;
}
