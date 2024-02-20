package com.web.stard.domain.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PasswordUpdateDto {

    private String newPassword;
}
