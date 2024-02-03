package com.web.stard.domain.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Profile {

    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private float credibility; // 신뢰도 (별점 평균)

    private String introduce; // 자기소개

    @Column(name = "img_name")
    private String imgName; // 이미지 이름

    @Column(name = "img_url")
    private String imgUrl; // 이미지 경로

}
