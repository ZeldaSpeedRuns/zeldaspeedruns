package com.zeldaspeedruns.zeldaspeedruns.security.user;

public class ZsrUserTestUtils {
    public static ZsrUser zsrUser(String username) {
        return new ZsrUser(username, username + "example.com", "password");
    }
}
