package com.tune.server.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tune.server.domain.Member;
import com.tune.server.dto.kakao.KakaoError;
import com.tune.server.dto.kakao.KakaoUserInfo;
import com.tune.server.dto.request.KakaoTokenRequest;
import com.tune.server.service.member.MemberService;
import com.tune.server.util.JwtUtil;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@ApiOperation(value = "Login", notes = "Authenticate user by login and password")
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "Successfully authenticated"),
        @ApiResponse(code = 401, message = "Invalid credentials")
})
@ApiImplicitParams({
        @ApiImplicitParam(name = "Authorization", value = "Basic authentication credentials", required = true, dataType = "string", paramType = "header")
})
@RequestMapping(value = "/login", method = RequestMethod.POST)
@SecurityRequirement(name = "basicAuth")
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
            try {
                KakaoTokenRequest kakaoTokenRequest = objectMapper.readValue(request.getInputStream(), KakaoTokenRequest.class);
                String refreshToken = kakaoTokenRequest.getRefresh_token();
                String accessToken = kakaoTokenRequest.getAccess_token();

                // 1. 카카오 토큰으로 회원 정보 조회
                KakaoUserInfo kakaoUserInfo = getKaKaoUserInfo(accessToken);

                // 2. 회원 정보로 로그인/회원가입 처리
                if (!memberService.isExistMember(kakaoUserInfo)) {
                    // 회원가입 처리
                    if (!memberService.signUp(kakaoUserInfo)) {
                        throw new RuntimeException("회원가입에 실패했습니다.");
                    }
                }

                // 3. JWT 토큰 발급
                Member member = memberService.getMember(kakaoUserInfo);
                String jwtToken = JwtUtil.generateJwt(member);

                // 4. 응답
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(objectMapper.writeValueAsString(jwtToken));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }

    private KakaoUserInfo getKaKaoUserInfo(String accessToken) throws JsonProcessingException {
        // KAKAO_USER_INFO_URL로 accessToken을 보내서 회원 정보를 받아온다.
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
                    throw new RuntimeException("카카오 토큰이 유효하지 않습니다.");
                case -1:
                    throw new RuntimeException("카카오 서버 오류입니다.");
                default:
                    throw new RuntimeException("카카오 토큰을 조회하는 중에 오류가 발생했습니다.");
            }
        }
    }
}
