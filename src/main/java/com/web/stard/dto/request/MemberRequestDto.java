package com.web.stard.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.NotEmpty;

public class MemberRequestDto {

    @Getter
    @Setter
    public static class Login {

        @NotEmpty(message = "이메일은 필수 입력값입니다.")
        private String memberId;

        @NotEmpty(message = "비밀번호는 필수 입력값입니다.")
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(memberId, password);
        }
    }

    @Getter
    @Setter
    public static class Reissue {

        @NotEmpty(message = "accessToken 을 입력해주세요.")
        private String accessToken;

        @NotEmpty(message = "refreshToken 을 입력해주세요.")
        private String refreshToken;
    }

    @Getter
    @Setter
    public static class Logout {

        @NotEmpty(message = "잘못된 요청입니다.")
        private String accessToken;

//        @NotEmpty(message = "잘못된 요청입니다.")
        private String refreshToken;

        private String memberId;
    }
}
