package com.tune.server.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tune.server.domain.Member;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {

    // JWT_SECRET_KEY is set in application.yml
    public static String JWT_SECRET_KEY;

    // refresh token expires in 6 months
    public static final int REFRESH_TOKEN_EXPIRES_MONTH = 6;

    // access token expires in 12 hours
    public static final int ACCESS_TOKEN_EXPIRE_MINUTE = 12 * 60;

    @Value("${external.jwt.secret}")
    public void setKey(String key) {
        JWT_SECRET_KEY = key;
    }

    public static Member getMemberFromJwt(String token, ObjectMapper objectMapper) {
        Map<String, Object> claims = Jwts.parserBuilder()
                .setSigningKey(JWT_SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return objectMapper.convertValue(claims, Member.class);
    }

    public static String generateJwt(Member member) {
        Date now = Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant());
        Date accessTokenExpiredDate = Date.from(LocalDateTime.now().plusMinutes(ACCESS_TOKEN_EXPIRE_MINUTE).atZone(ZoneId.of("Asia/Seoul")).toInstant());

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", member.getId());
        claims.put("name", member.getName());

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(accessTokenExpiredDate)
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)
                .compact();
    }

    public static boolean isValidJwt(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(JWT_SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isExpired(String token, Date date) {
        Date expiredDate = Jwts.parserBuilder()
                .setSigningKey(JWT_SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        return expiredDate.before(date);
    }

    public static String generateRefreshToken() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
