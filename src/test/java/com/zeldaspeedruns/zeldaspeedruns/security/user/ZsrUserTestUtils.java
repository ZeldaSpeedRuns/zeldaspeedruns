package com.zeldaspeedruns.zeldaspeedruns.security.user;

import com.zeldaspeedruns.zeldaspeedruns.security.PasswordUtils;
import com.zeldaspeedruns.zeldaspeedruns.security.userdetails.ZsrUserDetails;

public class ZsrUserTestUtils {
    public static ZsrUser zsrUser(String username) {
        String emailAddress = username + "@example.com";
        return zsrUser(username, emailAddress);
    }

    public static ZsrUser zsrUser(String username, String emailAddress) {
        String password = PasswordUtils.generatePassword(20);
        return new ZsrUser(username, emailAddress, password);
    }

    public static ZsrUserDetails zsrUserDetails(String username) {
        String emailAddress = username + "@example.com";
        return new ZsrUserDetails(zsrUser(username, emailAddress));
    }
}
