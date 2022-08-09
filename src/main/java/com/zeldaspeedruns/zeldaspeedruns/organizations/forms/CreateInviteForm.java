package com.zeldaspeedruns.zeldaspeedruns.organizations.forms;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class CreateInviteForm {
    @Min(0)
    Long maxUses = 0L;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    LocalDateTime expiresAt;
}
