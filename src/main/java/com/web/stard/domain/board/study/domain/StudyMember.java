package com.web.stard.domain.board.study.domain;

import com.web.stard.domain.member.domain.Member;
import lombok.*;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Builder
@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Study_Member")
public class StudyMember {      // 스터디 참여자

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    @Column(name = "reply_allow")
    private boolean replyAllow;

    @Column(name = "delete_allow")
    private boolean deleteAllow;

    @Column(name = "recruiter_allow")
    private boolean recruiterAllow;

}
