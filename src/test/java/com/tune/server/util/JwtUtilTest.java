package com.tune.server.util;

import com.tune.server.domain.Member;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


class JwtUtilTest {

    @BeforeAll
    public static void setup() {
        JwtUtil.JWT_SECRET_KEY = "Jo73VnKMoZCBEgBloGffXFTDsZxRZ9fN5geXS3nX0wE=";
    }

    @Test
    @DisplayName("JWT는 JWT_SECRET_KEY로 암호화된다.")
    void generateJwt() {
        // given
        Member member = Member.builder()
                .id(1L)
                .name("test")
                .build();
        String jwt = JwtUtil.generateJwt(member);

        // when & then
        assertNotNull(jwt);
        assertTrue(JwtUtil.isValidJwt(jwt));
    }

    @Test
    @DisplayName("JWT는 6개월 후에 만료된다.")
    void generateJwt2() {
        // given
        Member member = Member.builder()
                .id(1L)
                .name("test")
                .build();
        String jwt = JwtUtil.generateJwt(member);

        // when & then
        assertNotNull(jwt);
        assertTrue(JwtUtil.isValidJwt(jwt));
        assertFalse(JwtUtil.isExpired(jwt, Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant())));

        LocalDateTime after13hours = LocalDateTime.now().plusHours(13);
        assertFalse(JwtUtil.isExpired(jwt, Date.from(after13hours.atZone(ZoneId.of("Asia/Seoul")).toInstant())));
    }
    @Test
    @DisplayName("UUID로 이루어진 Refresh Token을 생성한다")
    void generateRefreshToken() {
        // given
        String refreshToken = JwtUtil.generateRefreshToken();

        // when & then
        assertNotNull(refreshToken);

        int uuid_size = UUID.randomUUID().toString().replace("-", "").length();
        assertEquals(uuid_size, refreshToken.length());
    }

    @Test
    @DisplayName("UUID는 중복되지 않아야 한다.")
    void generateRefreshToken2() {
        // given
        String refreshToken1 = JwtUtil.generateRefreshToken();
        String refreshToken2 = JwtUtil.generateRefreshToken();

        // when & then
        assertNotEquals(refreshToken1, refreshToken2);
    }
}