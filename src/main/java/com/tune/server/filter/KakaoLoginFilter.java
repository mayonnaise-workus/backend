package com.tune.server.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tune.server.domain.Member;
import com.tune.server.dto.kakao.KakaoError;
import com.tune.server.dto.kakao.KakaoUserInfo;
import com.tune.server.dto.request.KakaoTokenRequest;
import com.tune.server.dto.response.FullTokenResponse;
import com.tune.server.exceptions.login.InvalidTokenException;
import com.tune.server.exceptions.login.KakaoServerException;
import com.tune.server.exceptions.login.TokenNotFoundException;
import com.tune.server.service.member.MemberService;
import com.tune.server.util.JwtUtil;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;



public class KakaoLoginFilter extends OncePerRequestFilter {
    private final String SIGNUP_URI;
    private final String KAKAO_USER_INFO_URL = "https://kapi.kakao.com/v1/user/access_token_info";
    private final ObjectMapper objectMapper;

    private final MemberService memberService;

    public KakaoLoginFilter(String signupUri, ObjectMapper objectMapper, MemberService memberService) {
        this.SIGNUP_URI = signupUri;
        this.objectMapper = objectMapper;
        this.memberService = memberService;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (SIGNUP_URI.equals(request.getRequestURI()) && request.getMethod().equals("POST")) {
            KakaoTokenRequest kakaoTokenRequest = getKakaoToken(request);
            String refreshToken = kakaoTokenRequest.getRefresh_token();
            String accessToken = kakaoTokenRequest.getAccess_token();

            // 1. ????????? ???????????? ?????? ?????? ??????
            KakaoUserInfo kakaoUserInfo = getKaKaoUserInfo(accessToken);

//             1-1. Mock KakaoUserInfo
//            KakaoUserInfo kakaoUserInfo = KakaoUserInfo.builder()
//                            .app_id(new Random().nextInt(1000000000))
//                            .id((long) new Random().nextInt(1000000000))
//                            .expires_in(1000000000)
//                    .build();

            kakaoUserInfo.setRefreshToken(refreshToken);


            // 2. ?????? ????????? ?????????/???????????? ??????
            if (!memberService.isExistMember(kakaoUserInfo)) {
                // ???????????? ??????
                if (!memberService.signUp(kakaoUserInfo)) {
                    throw new InternalError("??????????????? ??????????????????.");
                }
            }

            // 3. JWT ?????? ??????
            Member member = memberService.getMember(kakaoUserInfo);
            FullTokenResponse fullTokenResponse = FullTokenResponse
                    .builder()
                    .access_token(JwtUtil.generateJwt(member))
                    .refresh_token(member.getRefreshToken())
                    .build();

            // 4. ??????
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(fullTokenResponse));
            return;
        }


        filterChain.doFilter(request, response);
    }

    private KakaoTokenRequest getKakaoToken(HttpServletRequest request) {
        try {
            KakaoTokenRequest kakaoTokenRequest = objectMapper.readValue(request.getInputStream(), KakaoTokenRequest.class);
            if (kakaoTokenRequest.getAccess_token() == null || kakaoTokenRequest.getRefresh_token() == null) {
                throw new TokenNotFoundException("access_token ?????? refresh_token??? ????????????.");
            }

            return kakaoTokenRequest;
        } catch (IOException e) {
            throw new TokenNotFoundException("????????? ????????? ???????????? ?????? ????????? ??????????????????." + e.getMessage());
        }
    }

    private KakaoUserInfo getKaKaoUserInfo(String accessToken) throws JsonProcessingException {
        // KAKAO_USER_INFO_URL??? accessToken??? ????????? ?????? ????????? ????????????.
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoUserInfo> response = restTemplate.exchange(KAKAO_USER_INFO_URL, HttpMethod.GET, httpEntity, KakaoUserInfo.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            KakaoError error = objectMapper.readValue(e.getResponseBodyAsString(), KakaoError.class);
            switch (error.getCode()) {
                case -401:
                case -2:
                    throw new InvalidTokenException("????????? ????????? ???????????? ????????????.");
                case -1:
                    throw new KakaoServerException("????????? ?????? ???????????????.");
                default:
                    throw new KakaoServerException("????????? ????????? ???????????? ?????? ????????? ??????????????????.");
            }
        }
    }
}
