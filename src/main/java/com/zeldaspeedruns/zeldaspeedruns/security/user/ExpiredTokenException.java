package com.zeldaspeedruns.zeldaspeedruns.security.user;

public class ExpiredTokenException extends Exception{
    public ExpiredTokenException(String message) {
        super(message);
    }
}
