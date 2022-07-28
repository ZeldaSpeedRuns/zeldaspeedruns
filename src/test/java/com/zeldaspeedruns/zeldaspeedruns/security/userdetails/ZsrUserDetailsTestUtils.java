package com.zeldaspeedruns.zeldaspeedruns.security.userdetails;

import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserTestUtils;

public class ZsrUserDetailsTestUtils {
    public static ZsrUserDetails zsrUserDetails(String username) {
        var user = ZsrUserTestUtils.zsrUser(username);
        return new ZsrUserDetails(user);
    }
}
