package com.web.stard.domain.member.application;

import com.web.stard.domain.member.dto.EmailDto;

import javax.mail.MessagingException;

public interface EmailService {

    void sendCodeToEmail(String toEmail) throws Exception;

    boolean verifiedCode(String email, String authCode) throws Exception;

    void sendEmailResetPw(EmailDto emailDto) throws MessagingException;
}
