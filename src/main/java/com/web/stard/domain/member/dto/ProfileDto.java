package com.web.stard.domain.member.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
@Builder
public class ProfileDto {

    private String introduce; // 자기소개

    private String imgName; // 이미지 이름

    private String imgUrl; // 이미지 경로

}
