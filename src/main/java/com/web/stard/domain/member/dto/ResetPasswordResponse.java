package com.web.stard.domain.member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResetPasswordResponse {

    private String email;

    private String accessToken;

    @Builder
    public ResetPasswordResponse(String email, String accessToken) {
        this.email = email;
        this.accessToken = accessToken;
    }
}
