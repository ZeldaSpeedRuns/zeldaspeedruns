package com.zeldaspeedruns.zeldaspeedruns.organization.forms;

import com.zeldaspeedruns.zeldaspeedruns.validation.constraints.Slug;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateOrganizationForm {
    @NotNull
    @Length(min = 4, max = 128)
    private String name;

    @Slug
    @NotNull
    @Length(min = 3, max = 32)
    private String slug;
}
