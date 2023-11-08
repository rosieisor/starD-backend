package com.web.stard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class CsrfConfig {
    @Bean
    public CookieCsrfTokenRepository csrfTokenRepository() {
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        // CSRF 토큰을 저장할 쿠키의 이름 (기본값 "XSRF-TOKEN")
        tokenRepository.setCookieName("XSRF-TOKEN");
        return tokenRepository;
    }
}