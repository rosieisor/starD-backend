package com.web.stard.domain.member.application;

import com.web.stard.global.config.jwt.JwtTokenProvider;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.global.dto.Response;
import com.web.stard.domain.member.dto.MemberRequestDto;
import com.web.stard.global.dto.TokenInfo;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.error.CustomException;
import com.web.stard.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class SignService {

    private final MemberRepository memberRepository;
    private final Response response;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate redisTemplate;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";


    public TokenInfo signIn(MemberRequestDto.SignInDto dto) {

        memberRepository.findById(dto.getMemberId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));

        try {
            // 1. Login ID/PW 를 기반으로 Authentication 객체 생성
            // 이때 authentication 는 인증 여부를 확인하는 authenticated 값이 false
            UsernamePasswordAuthenticationToken authenticationToken = dto.toAuthentication();

            // 2. 실제 검증(사용자 비밀번호 체크)이 이루어지는 부분
            // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            // 3. 인증 정보를 기반으로 JWT 토큰 생성
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

            // 4. RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
            redisTemplate.opsForValue()
                    .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
            return tokenInfo;

        } catch (BadCredentialsException exception) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }

    public TokenInfo reissue(HttpServletRequest request) {

        // 1. 만료된 Access Token 을 Header에서 추출
        String expiredAccessToken = resolveToken(request);

        // 2. 추출된 Access Token 에 담긴 Authentication 객체를 가져와 사용자의 id로 redis 에 저장된 Refresh Token 추출
        Authentication authentication = jwtTokenProvider.getAuthentication(expiredAccessToken);
        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());

        // 3. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refreshToken))
            throw new CustomException(ErrorCode.INVALID_TOKEN);

        // (추가) 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if (ObjectUtils.isEmpty(refreshToken))
            throw new CustomException(ErrorCode.NOT_FOUND);

        if (!refreshToken.equals(refreshToken))
            throw new CustomException(ErrorCode.MISMATCH_TOKEN);

        // 4. 새로운 토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // 5. RefreshToken Redis 업데이트
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return tokenInfo;
    }

    public void signOut(HttpServletRequest request) {

        try {
            // 1. Access Token 검증
            String accessToken = resolveToken(request);
            boolean validateToken = jwtTokenProvider.validateToken(accessToken);

            if (validateToken) {
                // 2. Access Token 에서 User email 추출
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

                // 3. Redis 에서 해당 User email 로 저장된 Refresh Token 존재 여부 확인 후 있을 경우 삭제
                if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null)
                    redisTemplate.delete("RT:" + authentication.getName());

                // 4. 유효 시간이 만료되지 않는 경우, 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
                Long expiration = jwtTokenProvider.getExpiration(accessToken);
                redisTemplate.opsForValue().set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);
            } else {
                // 만료된 access Token
                throw new CustomException(ErrorCode.EXPIRED_TOKEN);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public String authority(HttpServletRequest request) {

        try {
            String accessToken = resolveToken(request);
            boolean validateToken = jwtTokenProvider.validateToken(accessToken);

            if (validateToken) {
                Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
                Member member = memberRepository.findById(authentication.getName())
                        .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND));
                return member.getRoles().getRoleValue();
            } else {
                // 만료된 access Token
                throw new CustomException(ErrorCode.EXPIRED_TOKEN);
            }
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    public Boolean isAccessTokenExpired(HttpServletRequest request) {
        String accessToken = resolveToken(request);
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken))
            return false;
        return true;
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
