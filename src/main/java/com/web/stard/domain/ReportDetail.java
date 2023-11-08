package com.web.stard.domain;

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
    private Member member;

    @Enumerated(EnumType.STRING)
    private ReportReason reason;

    @Column(name = "custom_reason")
    private String customReason;
}
