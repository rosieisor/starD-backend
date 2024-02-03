package com.web.stard.domain.board.global.domain;

import com.web.stard.domain.board.global.domain.enums.ReportReason;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.global.domain.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter @Setter
public class ReportDetail extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "report_id")
    private Report report;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;  // 신고한 사람

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Column(name = "custom_reason")
    private String customReason;
}
