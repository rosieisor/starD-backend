package com.web.stard.domain.board.study.domain;

import com.web.stard.domain.member.domain.Member;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Builder
@Table
@Setter @Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Applicant {        // 스터디 신청자

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

    @CreatedDate
    @Column(name = "application_time", updatable = false)
    private LocalDateTime applicationTime;

    @Column(name = "participation_state")
    private Boolean participationState;     // 참여 상태 ( null : 개설자 선택 이전의 상태 / true : 수락 / false : 거절 )
    // null 값을 넣기 위해서는 boolean -> Boolean

    @Column(name = "apply_reason")
    private String applyReason;     // 지원 동기

}
