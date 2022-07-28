package com.zeldaspeedruns.zeldaspeedruns.organizations.projections;

public interface OrganizationWithMemberCount {
    Long getId();

    String getName();

    String getSlug();

    String getIcon();

    Long getMemberCount();
}
