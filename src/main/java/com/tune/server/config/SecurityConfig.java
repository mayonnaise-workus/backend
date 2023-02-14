package com.tune.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tune.server.filter.KakaoLoginFilter;
import com.tune.server.service.member.MemberService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;

    private final MemberService memberService;

    @Bean
    protected DefaultSecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
            .cors().disable()
            .csrf().disable()
            .authorizeRequests()
            .anyRequest().permitAll()
            .and()
            .addFilterBefore(new KakaoLoginFilter("/login/kakao", objectMapper, memberService), UsernamePasswordAuthenticationFilter.class)
            .build();
    }

}
