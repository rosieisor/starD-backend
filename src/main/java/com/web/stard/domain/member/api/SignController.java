package com.web.stard.domain.member.api;

import com.web.stard.domain.member.dto.MemberRequestDto;
import com.web.stard.domain.member.application.SignService;
import com.web.stard.global.dto.TokenInfo;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RequiredArgsConstructor
@RequestMapping("/user/auth")
@RestController
public class SignController {

    private final SignService signService;

    @ApiOperation(value = "로그인")
    @PostMapping("/sign-in")
    public ResponseEntity<TokenInfo> signIn(@Valid @RequestBody MemberRequestDto.SignInDto dto) {
        return ResponseEntity.ok().body(signService.signIn(dto));
    }

    @ApiOperation(value = "토큰 재발급")
    @PostMapping("/reissue")
    public ResponseEntity<TokenInfo> reissue(HttpServletRequest request) {
        return ResponseEntity.ok().body(signService.reissue(request));
    }

    @ApiOperation(value = "로그아웃")
    @PostMapping("/sign-out")
    public void signOut(HttpServletRequest request) {
        signService.signOut(request);
    }

    @GetMapping("/authority")
    public ResponseEntity<String> getAuthority(HttpServletRequest request) {
        return ResponseEntity.ok().body(signService.authority(request));
    }

    @GetMapping("/check")
    public ResponseEntity<String> check() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok().body(authentication.getName());
    }

    @GetMapping("/accessToken-expiration")
    public ResponseEntity<Boolean> isAccessTokenExpired(HttpServletRequest request) {
        return ResponseEntity.ok().body(signService.isAccessTokenExpired(request));
    }
}