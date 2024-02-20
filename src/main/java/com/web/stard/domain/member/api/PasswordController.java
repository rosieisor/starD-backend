package com.web.stard.domain.member.api;

import com.web.stard.domain.member.dto.EmailDto;
import com.web.stard.domain.member.application.EmailService;
import com.web.stard.domain.member.dto.ResetPasswordResponse;
import com.web.stard.domain.member.application.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;

@RestController
@RequiredArgsConstructor
public class PasswordController {

    private final EmailService emailService;

    private final MemberService memberService;

    @PostMapping("/find-password")
    public ResponseEntity<Void> findPassword(@RequestBody EmailDto emailDto) throws MessagingException {
        emailService.sendEmailResetPw(emailDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> verificationPassword(@RequestParam("token") String token) throws Exception {
        return ResponseEntity.ok().body(memberService.verificationPassword(token));
    }


}
