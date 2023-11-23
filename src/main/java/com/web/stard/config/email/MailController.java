package com.web.stard.config.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.mail.SimpleEmail;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/emails")
public class MailController {

    private final MailService mailService;

    @PostMapping("/verification-requests")
    public ResponseEntity sendMessage(@RequestBody EmailDto emailDto) throws Exception {
        mailService.sendCodeToEmail(emailDto.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/verifications")
    public Boolean verificationEmail(@RequestParam("email") String email, @RequestParam("authCode") String authCode)  {
        try {
            return mailService.verifiedCode(email, authCode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
