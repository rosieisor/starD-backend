package com.web.stard.domain;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter @Setter
public class StudyPost extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    @NotNull @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // 작성자

    @NotNull
    private String title; // 제목

    private String content; // 내용

    @NotNull @Column(name = "view_count")
    private int viewCount;

    @Column(name = "file_name")
    private String fileName; // 파일 이름

    @Column(name = "file_url")
    private String fileUrl; // 파일 경로



    @Transient // DB랑 매핑되지 않음
    private int starCount; // 공감 수



    public StudyPost(Study study, Member member, String title, String content) {
        this.study = study;
        this.member = member;
        this.title = title;
        this.content = content;
    }
}
