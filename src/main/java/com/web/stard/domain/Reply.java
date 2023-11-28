package com.web.stard.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

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
