package com.zeldaspeedruns.zeldaspeedruns.organizations.dto;

import lombok.Data;

@Data
public class OrganizationSummary {
    private String name;
    private String slug;
    private String icon;
    private Long memberCount;
}
