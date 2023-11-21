package com.web.stard.domain;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@Builder
public class StarScrap {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne
    @JoinColumn(name = "study_post_id")
    private StudyPost studyPost;

    @Enumerated(EnumType.STRING)
    private ActType type; // STAR or SCRAP

    @Enumerated(EnumType.STRING)
    private PostType tableType; // 게시글 타입 (COMM / STUDY, STUDYPOST)

    @NotNull @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;
}
