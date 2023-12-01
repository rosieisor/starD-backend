package com.web.stard.domain;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

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
