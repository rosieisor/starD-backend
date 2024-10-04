package com.web.stard.domain.member.application;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.dto.EmailDto;
import com.web.stard.domain.member.repository.MemberRepository;
import com.web.stard.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Random;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final MemberService memberService;

    private final JavaMailSender emailSender;
    private static final String AUTH_CODE_PREFIX = "AuthCode ";
    private static final String RESET_PW_SUBJECT = "[StarD] 비밀번호 재설정 안내 메일";

    private static final Long RESET_PW_TOKEN_EXPIRE_TIME = 12 * 60 * 60 * 1000L;

    private static final String RESET_PW_PREFIX = "ResetPwToken ";

    private final RedisUtil redisUtil;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    @Value("${spring.mail.username}")
    private String adminAccount;

    @Value("${base.front-end.url}")
    private String baseUrl;

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

        String sender = adminAccount + "@naver.com";
        message.setFrom(new InternetAddress(sender));
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
    @Override
    public void sendCodeToEmail(String toEmail) throws Exception {
        String authCode = this.createAuthCode();
        sendEmail(toEmail, authCode);

        // 이메일 인증 요청 시 인증 번호 Redis에 저장 ( key = "AuthCode " + Email / value = AuthCode )
        redisUtil.setData(AUTH_CODE_PREFIX + toEmail, authCode, authCodeExpirationMillis);

    }

    /*
    인증 코드를 검증하는 메서드.
    파라미터로 전달받은 이메일을 통해 Redis에 저장되어 있는 인증 코드를 조회한 후, 파라미터로 전달 받은 인증 코드와 비교한다.
    만약 두 코드가 동일하다면 true를, Redis에서 Code가 없거나 일치하지 않는다면 false를 반화한다.
     */
    @Override
    public boolean verifiedCode(String email, String authCode) throws Exception{
        String redisAuthCode = redisUtil.getData(AUTH_CODE_PREFIX + email);

        if (redisAuthCode == null)
            throw new Exception("시간 초과");

        boolean authResult = false;

        if (redisAuthCode.equals(authCode))
            authResult = true;

        return authResult;
    }

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public void sendEmailResetPw(EmailDto emailDto) throws MessagingException {

        Member member = memberService.findByEmail(emailDto.getEmail());

        String pwResetToken = UUID.randomUUID().toString();

        boolean isExist = true;

        while(isExist){

            String existEmail = redisUtil.getData(RESET_PW_PREFIX + pwResetToken);
            if (existEmail == null)
                break;
            else
                pwResetToken = UUID.randomUUID().toString();
        }

        String pwResetUrl = baseUrl + "/reset-password?token=" + pwResetToken;

        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(MimeMessage.RecipientType.TO, emailDto.getEmail());
        message.setSubject(RESET_PW_SUBJECT);

        String messageContent = "<h2>비밀번호 재설정 안내 </h2> <br>" +
                "<p>안녕하세요. " + member.getId() +" 님</p>" +
                "<p>본 메일은 비밀번호 재설정을 위해 StarD에서 발송하는 메일입니다. 12시간 이내에 " +
                "링크를 클릭하여 비밀번호 재설정을 완료해주세요.</p>" +
                "<a href=\"" + pwResetUrl + "\">비밀번호 재설정</a>";

        message.setText(messageContent, "UTF-8", "html");
        String sender = adminAccount + "@naver.com";
        message.setFrom(new InternetAddress(sender));

        try {
            // TODO 로그 삭제
            log.info("비밀번호 재설정 Url 생성: " + pwResetUrl);
            emailSender.send(message);
            redisUtil.setData(RESET_PW_PREFIX + pwResetToken, emailDto.getEmail(), RESET_PW_TOKEN_EXPIRE_TIME);
        } catch (MailException e) {
            e.printStackTrace();
            log.debug("MailService.sendEmail exception occur toEmail: {}", emailDto.getEmail());
            throw new IllegalArgumentException();
        }
    }

}
