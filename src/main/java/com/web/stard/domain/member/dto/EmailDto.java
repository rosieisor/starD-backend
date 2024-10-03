package com.web.stard.domain.member.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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
