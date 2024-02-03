package com.web.stard.domain.board.study.domain;

import com.sun.istack.NotNull;
import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.study.domain.enums.ProgressStatus;
import com.web.stard.domain.board.study.domain.enums.RecruitStatus;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.global.domain.BaseEntity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
public class Study extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private int capacity;

    @NotNull
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member recruiter;

    private String city;    // 시

    private String district;    // 구

    private String field; // 분야

    private String tags;    // 태그들

    @NotNull @Column(name = "on_off")
    private String onOff;      // 온/오프/무관

    @NotNull @Column(name = "activity_start")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate activityStart;        // 활동 시작 기간

    @NotNull @Column(name = "activity_deadline")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate activityDeadline;     // 활동 마감 기간

    @CreatedDate
    @Column(name = "recruitment_start", updatable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recruitmentStart;

    @NotNull @Column(name = "recruitment_deadline")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recruitmentDeadline;

//    @NotNull
//    @Enumerated(EnumType.STRING)
//    private StudyType status;  // 스터디 진행 상황 (진행 중, 진행 완료, 중단 등)

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "recruit_status")
    private RecruitStatus recruitStatus;  // 스터디 모집 현황 (모집 중, 모집 완료)

    @Enumerated(EnumType.STRING)
    @Column(name = "progress_status")
    private ProgressStatus progressStatus;  // 스터디 진행 상황 (진행 중, 진행 완료, 중단 등)

    @NotNull
    @Column(name = "view_count")
    private int viewCount;

    @Enumerated(EnumType.STRING)
    private PostType type; // post 타입 [COMM, QNA, NOTICE, FAQ, STUDY, REPLY]

}
