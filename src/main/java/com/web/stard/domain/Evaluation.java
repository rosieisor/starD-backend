package com.web.stard.domain;

import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@Entity
@Table
@ToString
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Getter @Setter
public class Evaluation {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // 작성자 (평가한 회원)

    @NotNull @ManyToOne
    @JoinColumn(name = "target_id")
    private Member target; // 평가 대상 회원

    @NotNull @ManyToOne
    @JoinColumn(name = "study_id")
    private Study study;

    private String reason; // 별점 사유

    @NotNull
    @Column(name = "star_rating")
    private double starRating; // 별점



    public Evaluation(Member member, Member target, Study study, String reason, double starRating) {
        this.member = member;
        this.target = target;
        this.study = study;
        this.reason = reason;
        this.starRating = starRating;
    }
}
