package com.web.stard.global.config.security;

import com.web.stard.global.config.jwt.JwtAuthenticationFilter;
import com.web.stard.global.config.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtTokenProvider jwtTokenProvider;
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
//            "/v3/api-docs/**",
//            "/swagger-ui/**"

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

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .cors().and()
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeRequests()
                .antMatchers(PERMIT_URL_ARRAY).permitAll()
//                .antMatchers("/api/v1/users/userTest").hasRole("USER")
//                .antMatchers("/api/v1/users/adminTest").hasRole("ADMIN")
                .anyRequest().authenticated()

                // 해당 url 요청에 대해서는 로그인 요구 X
//                .antMatchers("/", "/signup", "/checkDuplicateID", "/checkDuplicateNickname", "/login", "/current-member").permitAll() //TODO 주석 제거
                // admin 요청에 대해서는 ROLE_ADMIN 역할을 가지고 있어야 함
//                .antMatchers("/admin").hasRole("ADMIN")
                // 나머지 요청에 대해서는 로그인 요구 O
//                .anyRequest().authenticated() //TODO 주석 제거

                .and()
                // JwtAuthenticationFilter를 UsernamePasswordAuthentictaionFilter 전에 적용시킨다.
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class);


    }

    // 암호화에 필요한 PasswordEncoder Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
