package com.web.stard.domain.board.study.dto;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDate;

@SuppressWarnings("serial")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class StudyDto implements Serializable {

    @NotNull
    private String title;

    @NotNull
    private String content;

    @NotNull
    private int capacity;

    private String field; // 분야

    private String city;    // 시

    private String district;    // 구

    private String tags;    // 태그 들

    @NotNull
    private String onOff;      // 온/오프/무관

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate activityStart;        // 활동 시작 기간

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate activityDeadline;     // 활동 마감 기간

//    @NotNull
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    private LocalDate recruitment_start;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate recruitmentDeadline;

//    @Enumerated(EnumType.STRING)
//    private RecruitStatus recruitStatus;  // 스터디 모집 현황 (모집 중, 모집 완료)
//
//    @Enumerated(EnumType.STRING)
//    private ProgressStatus progressStatus;  // 스터디 진행 상황 (진행 중, 진행 완료, 중단 등)
//
//    private int view_count;

}
