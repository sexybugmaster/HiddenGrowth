package com.hiddengrowth.backend.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // MVP: API 테스트 편하게 (나중에 JWT로 교체)
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // swagger
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // public endpoints
                        .requestMatchers("/health", "/chat", "/analysis/**").permitAll()
                        // 나머지는 일단 막거나(추후)
                        .anyRequest().permitAll()
                )
                // 로그인 폼 끄기
                .httpBasic(Customizer.withDefaults())
                .formLogin(form -> form.disable());

        return http.build();
    }
}
