package com.web.stard.dto.response;

import lombok.Data;

@Data
public class MemLoginCheckDto {

    private String memberId;

    private long accessToken_expiration;

}
