package com.tune.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tune.server.domain.Member;
import com.tune.server.dto.apple.AppleAuthTokenDto;
import com.tune.server.dto.request.AppleLoginRequest;
import com.tune.server.dto.response.FullTokenResponse;
import com.tune.server.exceptions.login.TokenNotFoundException;
import com.tune.server.service.member.MemberService;
import com.tune.server.util.JwtUtil;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.NoArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AppleLoginFilter extends OncePerRequestFilter {
    private final String SIGNUP_URI = "/login/apple";
    private final String APPLE_TOKEN_URI = "https://appleid.apple.com/auth/token";
    private final String APPLE_AUDIENCE_URI = "https://appleid.apple.com";
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberService memberService;

    @Value("${external.apple.bundle-id}")
    private String bundleId;

    @Value("${external.apple.client-id}")
    private String clientId;

    @Value("${external.apple.team-id}")
    private String teamId;

    @Value("${external.apple.private-key}")
    private String appleSignKey;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(SIGNUP_URI) && request.getMethod().equals("POST")) {
            // 1. AppleLoginRequest 파싱
            AppleLoginRequest appleLoginRequest = getAppleLoginRequest(request);

            // 2. Apple JWT 생성 및 검증
            AppleAuthTokenDto appleAuthTokenDto = getAppleAuthTokenDto(appleLoginRequest);
            appleAuthTokenDto.setUser_id(appleAuthTokenDto.getUser_id());

            // 3. 회원정보로 로그인/회원가입
            if (!memberService.isExistMember(appleAuthTokenDto)) {
                // 회원가입
                if (!memberService.signUp(appleAuthTokenDto)) {
                    throw new InternalError("회원가입에 실패했습니다.");
                }
            }

            // 4. JWT 토큰 발급
            Member member = memberService.getMember(appleAuthTokenDto);
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
        }

        filterChain.doFilter(request, response);
    }

    private AppleAuthTokenDto getAppleAuthTokenDto(AppleLoginRequest appleLoginRequest) {
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", appleLoginRequest.getAuthorizationCode());
        params.add("client_id", bundleId);
        params.add("client_secret", generateAppleSignKey());
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);
        try {
            ResponseEntity<AppleAuthTokenDto> response = restTemplate.postForEntity(APPLE_TOKEN_URI, httpEntity, AppleAuthTokenDto.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Apple Auth Token Error");
        }
    }

    private String generateAppleSignKey() {
        try {
            return Jwts.builder()
                    .setHeaderParam("kid", clientId)
                    .setHeaderParam("alg", "ES256")
                    .setIssuer(teamId)
                    .setAudience(APPLE_AUDIENCE_URI)
                    .setSubject(bundleId)
                    .setExpiration(new Date(System.currentTimeMillis() + 864000))
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                    .compact();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Apple Sign Key Error");
        }
    }

    private AppleLoginRequest getAppleLoginRequest(HttpServletRequest request) {
        try {
            AppleLoginRequest appleLoginRequest = objectMapper.readValue(request.getInputStream(), AppleLoginRequest.class);
            if (appleLoginRequest.getAuthorizationCode() == null) {
                throw new TokenNotFoundException("Autorization Code is null");
            }

            return appleLoginRequest;
        } catch (Exception e) {
            throw new TokenNotFoundException("Apple Token Parse Error");
        }
    }

    private PrivateKey getPrivateKey() throws IOException {
        // PRIVATE_KEY 렌더링
        appleSignKey = appleSignKey.replace(' ', '\n');
        String SSH_SUFFIX = "\n-----END PRIVATE KEY-----";
        String SSH_PREFIX = "-----BEGIN PRIVATE KEY-----\n";
        appleSignKey = SSH_PREFIX + appleSignKey + SSH_SUFFIX;

        Reader pemReader = new StringReader(appleSignKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        return converter.getPrivateKey(object);
    }

}
