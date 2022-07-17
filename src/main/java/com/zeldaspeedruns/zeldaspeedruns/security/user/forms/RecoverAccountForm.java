package com.zeldaspeedruns.zeldaspeedruns.security.user.forms;

import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Data
public class RecoverAccountForm {
    @Email
    @Size(min = 4, max = 128)
    private String emailAddress;
}
