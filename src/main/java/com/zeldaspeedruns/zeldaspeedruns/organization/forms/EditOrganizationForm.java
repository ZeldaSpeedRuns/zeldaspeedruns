package com.zeldaspeedruns.zeldaspeedruns.organization.forms;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class EditOrganizationForm {
    @NotNull
    @Length(min = 4, max = 128)
    private String name;

    private String description;
}
