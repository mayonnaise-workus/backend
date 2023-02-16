package com.tune.server.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tune.server.domain.Member;
import com.tune.server.dto.MemberAuthDto;
import com.tune.server.exceptions.login.InvalidTokenException;
import com.tune.server.exceptions.login.TokenExpiredException;
import com.tune.server.exceptions.login.TokenNotFoundException;
import com.tune.server.service.member.MemberService;
import com.tune.server.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@AllArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private ObjectMapper objectMapper;
    private MemberService memberService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer "))
            throw new TokenNotFoundException("JWT Token does not begin with Bearer String with URL : " + request.getRequestURI());


        String token = authorizationHeader.split(" ")[1];
        if (!JwtUtil.isValidJwt(token))
            throw new InvalidTokenException("JWT Token is not valid with Jwt : " + token);

        if (JwtUtil.isExpired(token, Date.from(LocalDateTime.now().atZone(ZoneId.of("Asia/Seoul")).toInstant())))
            throw new TokenExpiredException("JWT Token is expired");

        // Member 식별
        MemberAuthDto member = JwtUtil.getMemberFromJwt(token, objectMapper);

        // 권한 부여
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        // UserDetail을 통해 인증된 사용자 정보를 SecurityContext에 저장
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return "/refresh".equals(request.getRequestURI());
    }
}
