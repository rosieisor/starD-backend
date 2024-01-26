package com.web.stard.config.email;

import javax.mail.MessagingException;

public interface EmailService {

    void sendCodeToEmail(String toEmail) throws Exception;

    boolean verifiedCode(String email, String authCode) throws Exception;

    void sendEmailResetPw(EmailDto emailDto) throws MessagingException;
}
