package com.zeldaspeedruns.zeldaspeedruns.security.user.forms;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import com.zeldaspeedruns.zeldaspeedruns.validation.constraints.MustMatch;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@MustMatch.List({
        @MustMatch(field = "passwordConfirmation", matchedField = "password"),
        @MustMatch(field = "emailAddressConfirmation", matchedField = "emailAddress")
})
public class RegistrationForm {
    @Length(min = 3, max = ZsrUser.USERNAME_MAX_LENGTH)
    private String username;

    @Email
    @Length(min = 8, max = ZsrUser.EMAIL_MAX_LENGTH)
    private String emailAddress;

    @NotEmpty
    private String emailAddressConfirmation;

    @Length(min = 2, max = 40)
    private String password;

    @NotEmpty
    private String passwordConfirmation;
}
