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
    public ResponseEntity sendMessage(@RequestParam("email") String email) throws Exception {
        mailService.sendCodeToEmail(email);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/verifications")
    public Boolean verificationEmail(@RequestParam("email") String email, @RequestParam("code") String authCode)  {
        try {
            return mailService.verifiedCode(email, authCode);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

//    @PostMapping("/mailTest")
//    public void mail() {
//        SimpleEmail email = new SimpleEmail();
//        email.setCharset("euc-kr");    email.setHostName("mail.somehost.com");  // SMTP 서버를 지정
//        email.addTo("madvirus@empal.com", "최범균"); // 수신자를 추가
//        email.setFrom("madvirus@madvirus.net", "범균"); // 보내는 사람 지정
//        email.setSubject("텍스트 테스트 메일입니다."); // 메일 제목
//        email.setContent("테스트 메일의 내용입니다.", "text/plain; charset=euc-kr");
//        email.send(); // 메일 발송
//    }


}
