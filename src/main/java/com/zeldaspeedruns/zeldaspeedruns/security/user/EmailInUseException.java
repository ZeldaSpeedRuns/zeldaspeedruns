package com.zeldaspeedruns.zeldaspeedruns.security.user;

public class EmailInUseException extends Exception{
    public EmailInUseException(String message) {
        super(message);
    }
}
