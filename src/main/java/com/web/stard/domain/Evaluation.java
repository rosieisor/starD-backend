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
    private float starRating; // 별점



    public Evaluation(Member member, Member target, Study study, String reason, float starRating) {
        this.member = member;
        this.target = target;
        this.study = study;
        this.reason = reason;
        this.starRating = starRating;
    }

    public float calculate(float originCredibility, int originCount) {
        /* originCredibility = 기존 신뢰도
         plusRating = 평가에 반영할 평점
         originCount = 기존 평가 인원수
         (기존평점 * (인원 + 1) + 매긴 점수) / ((인원 + 1) + 1)
         인원 + 1인 이유 : 기본으로 본인이 5점을 매긴 걸로 계산되어 있음 (기본 값) */
        float total = originCredibility * (originCount + 1) + starRating;
        return total / (originCount + 2);
    }
}
