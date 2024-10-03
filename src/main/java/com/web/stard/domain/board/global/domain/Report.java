package com.web.stard.domain.board.global.domain;

import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.study.domain.Study;
import com.web.stard.domain.board.study.domain.StudyPost;
import com.web.stard.domain.member.domain.Member;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Table
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter @Setter
public class Report {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")  // 신고된 회원
    private Member member;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    @ManyToOne
    @JoinColumn(name = "reply_id")
    private Reply reply;

    @ManyToOne
    @JoinColumn(name="study_post_id")
    private StudyPost studyPost;

    @NotNull
    @Column(name = "table_type")
    @Enumerated(EnumType.STRING)
    private PostType tableType;    // [COMM, QNA, STUDY, REPLY, STUDYPOST]
}
