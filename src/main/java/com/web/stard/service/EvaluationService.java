package com.web.stard.service;

import com.web.stard.domain.Evaluation;
import com.web.stard.domain.Member;
import com.web.stard.domain.Study;
import com.web.stard.repository.EvaluationRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Getter @Setter
@AllArgsConstructor
@Service
public class EvaluationService {

    EvaluationRepository evaluationRepository;
    MemberService memberService;
    StudyService studyService;


    public Evaluation findById(Long evaluationId) {
        Optional<Evaluation> result = evaluationRepository.findById(evaluationId);

        if (result.isPresent()) {
            return result.get();
        } return null;
    }

    /* 평가 당한 내역 리스트 전체 조회 (전체 스터디) */
    public List<Evaluation> getMyEvaluationList(Authentication authentication) {
        Member target = memberService.find(authentication.getName());

        return evaluationRepository.findByTargetOrderByStudyActivityDeadlineDesc(target);
    }

    /* 평가 당한 내역 리스트 조회 (스터디 별로) */
    public List<Evaluation> getMyEvaluationListByStudy(Long studyId, Authentication authentication) {
        Member target = memberService.find(authentication.getName());
        Study study = studyService.findById(studyId);

        return evaluationRepository.findByTargetAndStudyOrderByStudyActivityDeadlineDesc(target, study);
    }

    /* 평가 당한 내역 상세 조회 */
    public Evaluation getMyEvaluation(Long evaluationId, Authentication authentication) {
        Evaluation evaluation = findById(evaluationId);

        if (evaluation != null) {
            if (evaluation.getTarget().getId().equals(authentication.getName())) {
                // 본인 평가만 조회 가능
                return evaluation;
            }
        }

        return null;
    }

    /* 자신이 한 평가 전체 조회 */
    public List<Evaluation> getEvaluationList(Authentication authentication) {
        Member member = memberService.find(authentication.getName());

        return evaluationRepository.findByMemberOrderByStudyActivityDeadlineDesc(member);
    }

    /* 자신이 한 평가 스터디별 조회 */
    public List<Evaluation> getEvaluationListByStudy(Long studyId, Authentication authentication) {
        Member member = memberService.find(authentication.getName());
        Study study = studyService.findById(studyId);

        return evaluationRepository.findByMemberAndStudyOrderByStudyActivityDeadlineDesc(member, study);
    }

    /* 자신이 한 평가 상세 조회 */
    public Evaluation getEvaluation(Long evaluationId, Authentication authentication) {
        Evaluation evaluation = findById(evaluationId);

        if (evaluation != null) {
            if (evaluation.getMember().getId().equals(authentication.getName())) {
                // 본인이 한 평가만 조회 가능
                return evaluation;
            }
        }

        return null;
    }


    /* 평가 등록 */
    public Evaluation registerEvaluation(Long studyId, String targetId, double starRating,
                                         String reason, Authentication authentication) {
        Study study = studyService.findById(studyId);
        Member member = memberService.find(authentication.getName()); // 작성자 (평가한 회원)
        Member target = memberService.find(targetId); // 평가 대상 회원

        Evaluation evaluation = new Evaluation(member, target, study, reason, starRating);

        evaluationRepository.save(evaluation);

        return evaluation;
    }

    /* 평가 수정 */
    public Evaluation updateEvaluation(Long evaluationId, double starRating,
                                       String reason, Authentication authentication) {
        Evaluation evaluation = findById(evaluationId);

        if (!evaluation.getMember().getId().equals(authentication.getName())) {
            // 자신이 한 평가가 아니면 수정 불가
            return null;
        }

        evaluation.setStarRating(starRating);
        evaluation.setReason(reason);

        evaluationRepository.save(evaluation);

        return evaluation;
    }

    /* 평가 삭제 */
    public boolean deleteEvaluation(Long evaluationId, Authentication authentication) {
        Evaluation evaluation = findById(evaluationId);

        if (!evaluation.getMember().getId().equals(authentication.getName())) {
            // 자신이 한 평가가 아니면 삭제 불가
            return false;
        }

        evaluationRepository.delete(evaluation);

        if (findById(evaluationId) == null) { // 삭제됨
            return true;
        } return false;
    }

}
