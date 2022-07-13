package com.zeldaspeedruns.zeldaspeedruns.security.user;

public class InvalidTokenException extends Exception{
    public InvalidTokenException(String message) {
        super(message);
    }
}
