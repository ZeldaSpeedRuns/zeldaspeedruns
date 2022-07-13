package com.zeldaspeedruns.zeldaspeedruns.security.user;

public class UsernameInUseException extends Exception{
    public UsernameInUseException(String message) {
        super(message);
    }
}
