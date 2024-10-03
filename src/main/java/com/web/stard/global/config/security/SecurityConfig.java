package com.web.stard.global.config.security;

import com.web.stard.domain.member.application.CustomMemberDetailsService;
import com.web.stard.global.config.jwt.JwtAuthenticationFilter;
import com.web.stard.global.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomMemberDetailsService customMemberDetailsService;
    private final RedisTemplate redisTemplate;

    private static final String[] PERMIT_URL_ARRAY = {

            /* swagger v2 */
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",


            /* swagger v3 */
            "/v3/api-docs/**",
            "/swagger-ui/**",

            "/user/auth/**",

            "/api/v2/studies/**",    // TODO URL 수정

            "/checkDuplicateNickname",
            "/checkDuplicateID",
            "/signup",
            "/signup/option-data",

            "/imageTest",

            "/user/mypage/profile/**",

            // 전체 스터디 게시글 조회 허용 O
            "/api/v2/studies/all",

            "/api/v2/studies/study-ranking",

            "/api/v2/studies/search-by-title",
            "/api/v2/studies/search-by-content",
            "/api/v2/studies/search-by-recruiter",
            "/member/find-nickname",
            "/member/find-id",

            "/com/**",
            "/notice/**",
            "/faq/**",
            "/qna/**",

            "/gs-guide-websocket",

            // 이메일 인증
            "/emails/verifications",
            "/emails/verification-requests",

            "/find-password",
            "/reset-password",

            // 알림
            "/notifications/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests((authorize) -> authorize
                        .requestMatchers(PERMIT_URL_ARRAY).permitAll()
                        .anyRequest().authenticated())
                // JwtAuthenticationFilter를 UsernamePasswordAuthenticationFilter 전에 적용
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // 암호화에 필요한 PasswordEncoder Bean 등록
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


}
