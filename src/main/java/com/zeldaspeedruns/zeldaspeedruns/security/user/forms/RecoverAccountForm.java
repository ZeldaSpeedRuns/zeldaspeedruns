package com.zeldaspeedruns.zeldaspeedruns.security.user.forms;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

@Data
public class RecoverAccountForm {
    @Email
    @NotEmpty
    private String emailAddress;
}
