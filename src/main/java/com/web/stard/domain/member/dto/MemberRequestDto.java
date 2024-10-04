package com.web.stard.domain.member.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import jakarta.validation.constraints.NotEmpty;

public class MemberRequestDto {

    @Getter
    @Setter
    public static class SignInDto {

        @NotEmpty(message = "이메일은 필수 입력값입니다.")
        private String memberId;

        @NotEmpty(message = "비밀번호는 필수 입력값입니다.")
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(memberId, password);
        }
    }

}
