package com.zeldaspeedruns.zeldaspeedruns.organizations;

public class OrganizationSlugInUseException extends RuntimeException {
    public OrganizationSlugInUseException(String message) {
        super(message);
    }
}
