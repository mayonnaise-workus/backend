package com.tune.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tune.server.filter.*;
import com.tune.server.service.member.MemberService;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    private final AppleLoginFilter appleLoginFilter;

    private final GoogleLoginFilter googleLoginFilter;
    private final MemberService memberService;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().antMatchers(
                "/",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-resources/**",
                "/h2-console/**",
                "/favicon.ico"
        );
    }

    @Bean
    protected DefaultSecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
                .cors().disable()
                .csrf().disable()
                .formLogin().disable()
                .authorizeRequests()
                    .antMatchers("/refresh").permitAll()
                    .antMatchers("/login/**").authenticated()
                    .antMatchers("/member/**").authenticated()
                .and()
                .addFilterBefore(new KakaoLoginFilter("/login/kakao", objectMapper, memberService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(appleLoginFilter, KakaoLoginFilter.class)
                .addFilterBefore(googleLoginFilter, AppleLoginFilter.class)
                .addFilterBefore(new ExceptionHandlerFilter(), GoogleLoginFilter.class)
                .addFilterAfter(new JwtAuthenticationFilter(objectMapper, memberService), KakaoLoginFilter.class)
                .build();
    }



}
