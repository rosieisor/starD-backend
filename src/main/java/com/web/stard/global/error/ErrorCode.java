package com.web.stard.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원"),
    CONFLICT(HttpStatus.CONFLICT, "중복"),
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "토큰 검증 실패");

    private final HttpStatus httpStatus;
    private final String message;
}
