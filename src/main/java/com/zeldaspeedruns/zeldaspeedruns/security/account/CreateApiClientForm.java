package com.zeldaspeedruns.zeldaspeedruns.security.account;

import com.zeldaspeedruns.zeldaspeedruns.validation.constraints.OAuth2RedirectUrls;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateApiClientForm {
    @NotEmpty
    @Size(min = 2, max = 32)
    private String name;

    @NotEmpty
    @OAuth2RedirectUrls
    private String redirectUrls;
}
