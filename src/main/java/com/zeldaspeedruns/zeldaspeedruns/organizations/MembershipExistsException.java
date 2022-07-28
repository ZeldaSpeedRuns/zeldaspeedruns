package com.zeldaspeedruns.zeldaspeedruns.organizations;

public class MembershipExistsException extends RuntimeException {
    public MembershipExistsException(String message) {
        super(message);
    }
}
