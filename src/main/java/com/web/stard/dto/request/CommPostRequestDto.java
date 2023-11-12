package com.web.stard.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommPostRequestDto {
    private Long id;
    private String title;
    private String content;
    private String category;
}
