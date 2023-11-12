package com.web.stard.config.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService{

    private final JavaMailSender emailSender;


    private static final String AUTH_CODE_PREFIX = "AuthCode ";

    private final RedisTemplate redisTemplate;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    /*
    sendEmail( ): 이메일을 발송하는 메서드 파라미터
     */
    public void sendEmail(String to, String authCode) throws Exception {
        MimeMessage emailForm = createEmailForm(to, authCode);
        try {
            emailSender.send(emailForm); // 메일 발송
        } catch (MailException e) {
            e.printStackTrace();
            log.debug("MailService.sendEmail exception occur toEmail: {}", to);
            throw new IllegalArgumentException();
        }
    }

    /*
    createEmailForm(): 발송할 이메일 데이터를 설정하는 메서드
     */
    private MimeMessage createEmailForm(String to, String authCode) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("StarD 인증 번호");

        // 메일 내용
        String text = "";
        text += "요청하신 인증 번호입니다.";
        text += authCode;
        message.setText(text);

        //보내는 사람의 메일 주소, 보내는 사람 이름
//        message.setFrom(new InternetAddress(id,"prac_Admin"));

        return message;
    }

    // 인증 번호 만들기
    public String createAuthCode() {
        StringBuffer key = new StringBuffer();

        Random random = new Random();

        for (int i = 0; i < 6; i++) // 인증코드 6자리
            key.append((random.nextInt(10)));

        return key.toString();
    }

    /*
    인증 코드를 생성 후 수신자 이메일로 발송하는 메서드.
    이후 인증 코드를 검증하기 위해 생성한 인증 코드를 Redis에 저장한다.
     */
    public void sendCodeToEmail(String toEmail) throws Exception {
        System.out.printf("sendCodeToEmail 진입 toEmail: "+ toEmail);
        String authCode = this.createAuthCode();
        System.out.printf("sendCodeToEmail 진입 authCode: "+ authCode);
        sendEmail(toEmail, authCode);

        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisTemplate.opsForValue().set(AUTH_CODE_PREFIX + toEmail, authCode, Duration.ofMillis(authCodeExpirationMillis));

    }

    /*
    인증 코드를 검증하는 메서드.
    파라미터로 전달받은 이메일을 통해 Redis에 저장되어 있는 인증 코드를 조회한 후, 파라미터로 전달 받은 인증 코드와 비교한다.
    만약 두 코드가 동일하다면 true를, Redis에서 Code가 없거나 일치하지 않는다면 false를 반화한다.
     */

    public boolean verifiedCode(String email, String authCode) {
        String redisAuthCode = (String) redisTemplate.opsForValue().get(AUTH_CODE_PREFIX + email);

        boolean authResult;

        if (redisAuthCode.equals(authCode))
            authResult = true;
        else
            authResult = false;

        return authResult;
    }

}
