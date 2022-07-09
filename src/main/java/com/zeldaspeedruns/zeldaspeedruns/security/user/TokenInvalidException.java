package com.zeldaspeedruns.zeldaspeedruns.security.user;

public class TokenInvalidException extends Exception {
    public TokenInvalidException(String message) {
        super(message);
    }
}
