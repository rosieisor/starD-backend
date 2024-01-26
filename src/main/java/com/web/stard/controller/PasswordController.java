package com.web.stard.controller;

import com.web.stard.config.email.EmailDto;
import com.web.stard.config.email.EmailService;
import com.web.stard.dto.ResetPasswordResponse;
import com.web.stard.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(HttpServletRequest request, @RequestParam("token") String token) throws Exception {

        return ResponseEntity.ok().body(memberService.resetPassword(request, token));
    }


}
