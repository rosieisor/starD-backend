package com.web.stard.domain.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileResponse {
    private Long id;
    private String memberId; // 사용자 아이디
    private String nickname; // 사용자 닉네임
    private String introduce; // 사용자 자기소개
    private float credibility; // 사용자 신뢰도 (별점 평균)
    private String imgName; // 사용자 프로필 이미지 이름
    private String imgUrl; // 사용자 프로필 이미지 경로
}
