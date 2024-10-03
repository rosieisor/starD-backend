package com.web.stard.domain.board.global.domain;

import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.study.domain.Study;
import com.web.stard.domain.board.study.domain.StudyPost;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.global.domain.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Table
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Reply  extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne
    @JoinColumn(name="study_post_id")
    private StudyPost studyPost;

    @NotNull
    private String content;

    @Enumerated(EnumType.STRING)
    private PostType type; // [COMM, QNA, STUDY, STUDYPOST]
}
