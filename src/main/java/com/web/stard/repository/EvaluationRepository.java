package com.web.stard.repository;

import com.web.stard.domain.Evaluation;
import com.web.stard.domain.Member;
import com.web.stard.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

    // 조회 정렬 : 활동 마감 기한이 최신인 스터디부터 보이게 (활동이 최근에 끝난 스터디의 평가부터 보이게)

    /* 평가 당한 내역 리스트 전체 조회 (전체 스터디) */
    List<Evaluation> findByTargetOrderByStudyActivityDeadlineDesc(Member target);

    /* 평가 당한 내역 리스트 조회 (스터디 별로) */
    List<Evaluation> findByTargetAndStudyOrderByStudyActivityDeadlineDesc(Member target, Study study);

    /* 자신이 한 평가 전체 조회 */
    List<Evaluation> findByMemberOrderByStudyActivityDeadlineDesc(Member member);

    /* 자신이 한 평가 스터디별 조회 */
    List<Evaluation> findByMemberAndStudyOrderByStudyActivityDeadlineDesc(Member member, Study study);
}
