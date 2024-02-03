package com.web.stard.domain.member.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailDto {

    @NotNull
    private String email;

    @Builder
    public EmailDto(String email) {
        this.email = email;
    }
}
