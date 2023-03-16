package com.tune.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.tune.server.domain.Member;
import com.tune.server.dto.request.AppleLoginRequest;
import com.tune.server.dto.request.GoogleLoginRequest;
import com.tune.server.dto.response.FullTokenResponse;
import com.tune.server.service.member.MemberService;
import com.tune.server.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class GoogleLoginFilter extends OncePerRequestFilter {
    private final String SIGNUP_URI = "/login/google";

    @Value("${external.google.client-id}")
    private String clientId;

    @Value("${external.google.client-secret}")
    private String clientSecret;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(SIGNUP_URI) && request.getMethod().equals("POST")) {
            try {
                // 1. GoogleLoginRequest 파싱
                GoogleLoginRequest googleLoginRequest = getGoogleLoginRequest(request);

                // 2. Google 검증
                GoogleTokenResponse googleTokenResponse = new GoogleAuthorizationCodeTokenRequest(
                        new NetHttpTransport(),
                        GsonFactory.getDefaultInstance(),
                        "https://oauth2.googleapis.com/token",
                        clientId,
                        clientSecret,
                        googleLoginRequest.getServerAuthCode(),
                        ""
                ).set("access_type", "offline").set("prompt", "consent").execute();

                // 3. Google 회원가입 및 로그인
                if (!memberService.isExistMember(googleLoginRequest)) {
                    // 회원가입
                    if (!memberService.signUp(googleLoginRequest, googleTokenResponse)) {
                        throw new InternalError("회원가입에 실패했습니다.");
                    }
                }

                // 4. JWT 토큰 발급
                Member member = memberService.getMember(googleLoginRequest);
                FullTokenResponse fullTokenResponse = FullTokenResponse
                        .builder()
                        .access_token(JwtUtil.generateJwt(member))
                        .refresh_token(member.getRefreshToken())
                        .build();

                // 5. 응답
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(fullTokenResponse));

                return;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Google 로그인 실패 - " + e.getMessage());
            }
        }
        doFilter(request, response, filterChain);
    }

    private GoogleLoginRequest getGoogleLoginRequest(HttpServletRequest request) {
        try {
            return objectMapper.readValue(request.getInputStream(), GoogleLoginRequest.class);
        } catch (IOException e) {
            throw new RuntimeException("GoogleLoginRequest 파싱 실패");
        }
    }
}
