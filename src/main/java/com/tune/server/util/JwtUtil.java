package com.tune.server.util;

import com.tune.server.domain.Member;

public class JwtUtil {

    // refresh token expires in 6 months
    public static final int REFRESH_TOKEN_EXPIRES_MONTH = 6;

    // access token expires in 12 hours
    public static final int ACCESS_TOKEN_EXPIRE_MINUTE = 12 * 60;

    public static String generateJwt(Member member) {
        return "jwt";
    }

    public static String generateRefreshToken() {
        return "refreshToken";
    }
}
