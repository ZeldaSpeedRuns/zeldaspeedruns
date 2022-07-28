package com.zeldaspeedruns.zeldaspeedruns;

public class InvalidSlugException extends IllegalArgumentException {
    public InvalidSlugException(String message) {
        super(message);
    }
}
