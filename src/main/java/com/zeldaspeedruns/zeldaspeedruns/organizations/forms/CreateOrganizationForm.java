package com.zeldaspeedruns.zeldaspeedruns.organizations.forms;

import com.zeldaspeedruns.zeldaspeedruns.validation.constraints.Slug;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateOrganizationForm {
    @NotNull
    @Size(min = 4, max = 128)
    private String name;

    @NotNull
    @Size(min = 2, max = 32)
    @Slug
    private String slug;
}
