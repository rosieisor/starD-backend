package com.web.stard.config.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/emails")
public class MailController {

    private final MailService mailService;

    @PostMapping("/verification-requests")
    public ResponseEntity sendMessage(@RequestParam("email") String email) throws Exception {
        System.out.println("url로 들어온 이메일: " + email);
        mailService.sendCodeToEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/verifications")
    public boolean verificationEmail(@RequestParam("email") String email, @RequestParam("code") String authCode) {
        return mailService.verifiedCode(email, authCode);
    }


}
