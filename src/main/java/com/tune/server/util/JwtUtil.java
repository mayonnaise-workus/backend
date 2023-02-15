package com.tune.server.util;

import com.tune.server.domain.Member;

public class JwtUtil {

    public static String generateJwt(Member member) {
        return "jwt";
    }

    public static String generateRefreshToken() {
        return "refreshToken";
    }
}
