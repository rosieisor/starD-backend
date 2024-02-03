package com.web.stard.domain.board.community.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CommPostRequestDto {
    private Long id;
    private String title;
    private String content;
    private String category;
}
