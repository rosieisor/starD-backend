package com.web.stard.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter @Setter
public class Post extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    private String title; // 제목
    @NotNull @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // 작성자
    @NotNull @Lob
    private String content; // 내용
    @NotNull
    private String category; // 카테고리 (취미 / 공부 / 잡담)

    @Enumerated(EnumType.STRING)
    private PostType type; // post 타입 [COMM, QNA, NOTICE, FAQ]

    @NotNull @Column(name = "view_count")
    private int viewCount;



    @Transient // DB랑 매핑되지 않음
    private int starCount; // 공감 수

    @Transient // DB랑 매핑되지 않음
    private int scrapCount; // 스크랩 수
}
