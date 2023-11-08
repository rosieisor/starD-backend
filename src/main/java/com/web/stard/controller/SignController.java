package com.web.stard.controller;

import com.web.stard.config.jwt.JwtTokenProvider;
import com.web.stard.config.lib.Helper;
import com.web.stard.config.security.SecurityUtil;
import com.web.stard.domain.Member;
import com.web.stard.dto.Response;
import com.web.stard.dto.request.MemberRequestDto;
import com.web.stard.dto.response.MemLoginCheckDto;
import com.web.stard.service.SignService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v2/members")
@RestController
public class SignController {

    private final JwtTokenProvider jwtTokenProvider;
    private final SignService signService;
    private final Response response;
    private final RedisTemplate redisTemplate;

    @GetMapping("/current-member")
    public String getCurrentMember() {
//    public String getCurrentMember(@AuthenticationPrincipal Member member) {
//        System.out.println(member);
//        System.out.println("test" + SecurityUtil.getCurrentUserEmail());
//        return SecurityUtil.getCurrentUserEmail();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return "Current user: " + username;
//        // 현재 로그인한 사용자 정보 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null && authentication.isAuthenticated()) {
//            String username = authentication.getName(); // 사용자의 아이디 (username) 가져오기
//
//            System.out.println("로그인 O " + username);
//
//            return username;
//        } else {
//            System.out.println("로그인 X");
//            return null;
//        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(MemberRequestDto.Login login) {
        return signService.login(login);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@Validated MemberRequestDto.Reissue reissue, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        return signService.reissue(reissue);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Validated MemberRequestDto.Logout logout, Errors errors) {

        String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + logout.getMemberId());
        logout.setRefreshToken(refreshToken);

        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        return signService.logout(logout);
    }

    @GetMapping("/authority")
    public ResponseEntity<?> authority() {
        log.info("ADD ROLE_ADMIN");
        return signService.authority();
    }

    @GetMapping("/userTest")
    public ResponseEntity<?> userTest() {
        log.info("ROLE_USER TEST");
        return response.success();
    }

    @GetMapping("/adminTest")
    public ResponseEntity<?> adminTest() {
        log.info("ROLE_ADMIN TEST");
        return response.success();
    }


    @GetMapping("/check")
    public String check() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    @GetMapping("/accessToken-expiration")
    public boolean isAccessTokenExpired(@RequestParam String accessToken) {
        if (accessToken != null && jwtTokenProvider.validateToken(accessToken))
            return false;
        return true;
    }
}