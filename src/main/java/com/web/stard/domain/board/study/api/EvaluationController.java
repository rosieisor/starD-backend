package com.web.stard.domain.board.study.api;

import com.web.stard.domain.board.study.domain.Evaluation;
import com.web.stard.domain.board.study.application.EvaluationService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@RestController
@RequestMapping("/rate")
public class EvaluationController {

    private final EvaluationService evaluationService;


    /* 자신이 한 평가 전체 조회 */
    @GetMapping("/member")
    public List<Evaluation> getEvaluationList(Authentication authentication) {
        return evaluationService.getEvaluationList(authentication);
    }

    /* 자신이 한 평가 스터디별 조회 */
    @GetMapping("/member/{studyId}")
    public List<Evaluation> getEvaluationListByStudy(@PathVariable(name = "studyId") Long studyId, Authentication authentication) {
        return evaluationService.getEvaluationListByStudy(studyId, authentication);
    }

    /* 자신이 한 평가 상세 조회 */
    @GetMapping("/member/detail/{evaluationId}")
    public Evaluation getEvaluation(@PathVariable(name = "evaluationId") Long evaluationId, Authentication authentication) {
        return evaluationService.getEvaluation(evaluationId, authentication);
    }


    /* 평가 등록 */
    @PostMapping
    public Evaluation registerEvaluation(@RequestParam(name = "studyId") Long studyId, @RequestParam(name = "targetId") String targetId,
                                         @RequestParam(name = "starRating") float starRating, @RequestParam(name = "reason") String reason,
                                         Authentication authentication) {
        return evaluationService.registerEvaluation(studyId, targetId, starRating, reason, authentication);
    }
}
