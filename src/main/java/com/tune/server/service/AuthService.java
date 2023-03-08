package com.tune.server.service;

import com.tune.server.filter.AppleLoginFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;

@Service
public class AuthService {
    private final String APPLE_REVOKE_URL = "https://appleid.apple.com/auth/revoke";
    private final String APPLE_AUDIENCE_URI = "https://appleid.apple.com";

    @Value("${external.apple.bundle-id}")
    private String bundleId;

    @Value("${external.apple.client-id}")
    private String clientId;

    @Value("${external.apple.team-id}")
    private String teamId;

    @Value("${external.apple.private-key}")
    private String appleSignKey;

    private final String GOOGLE_REVOKE_URL = "https://oauth2.googleapis.com/revoke?token=";

    private final String KAKAO_REVOKE_URL = "https://kapi.kakao.com/v2/user/revoke/scopes";
    private final String KAKAO_SCOPRES_URL = "https://kapi.kakao.com/v2/user/scopes";

    @Value("${external.kakao.admin-key}")
    private String kakaoAdminKey;

    public boolean revokeAppleToken(String refreshToken) {
        // POST to APPLE_REVOKE_URL
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", bundleId);
        params.add("client_secret", generateAppleSignKey());
        params.add("token", refreshToken);
        params.add("token_type_hint", "refresh_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        try {
            restTemplate.postForEntity(APPLE_REVOKE_URL, httpEntity, String.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean revokeGoogleToken(String refreshToken) {
        // POST to GOOGLE_REVOKE_URL
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        String googleRevokeUrl = GOOGLE_REVOKE_URL + refreshToken;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

        try {
            restTemplate.postForEntity(googleRevokeUrl, httpEntity, String.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean revokeKakaoToken(Long userId) {
        // 동의 내역 철회 하기
        RestTemplate restTemplate = new RestTemplateBuilder().build();

        LinkedMultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("target_id_type", "user_id");
        params.add("target_id", userId);
        params.add("scopes", "[\"account_ci\"]");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Authorization", "KakaoAK " + kakaoAdminKey);

        HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(params, headers);

        try {
            restTemplate.postForEntity(KAKAO_REVOKE_URL, httpEntity, String.class);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public PrivateKey getPrivateKey() throws IOException {
        // PRIVATE_KEY 렌더링
        String fullAppleSignKey = appleSignKey.replace(' ', '\n');
        String SSH_SUFFIX = "\n-----END PRIVATE KEY-----";
        String SSH_PREFIX = "-----BEGIN PRIVATE KEY-----\n";
        fullAppleSignKey = SSH_PREFIX + fullAppleSignKey + SSH_SUFFIX;

        Reader pemReader = new StringReader(fullAppleSignKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        return converter.getPrivateKey(object);
    }

    public String generateAppleSignKey() {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());

        try {
            return Jwts.builder()
                    .setHeaderParam("kid", clientId)
                    .setHeaderParam("alg", "ES256")
                    .setIssuer(teamId)
                    .setAudience(APPLE_AUDIENCE_URI)
                    .setSubject(bundleId)
                    .setExpiration(expirationDate)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                    .compact();
        } catch (Exception e) {
            throw new IllegalArgumentException("Apple Sign Key Error - " + e.getMessage() + " - expiredAt : " + expirationDate);
        }
    }

}
