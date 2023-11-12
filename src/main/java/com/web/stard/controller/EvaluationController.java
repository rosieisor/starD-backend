package com.web.stard.controller;

import com.web.stard.domain.Evaluation;
import com.web.stard.service.EvaluationService;
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
    public List<Evaluation> getEvaluationListByStudy(@PathVariable Long studyId, Authentication authentication) {
        return evaluationService.getEvaluationListByStudy(studyId, authentication);
    }

    /* 자신이 한 평가 상세 조회 */
    @GetMapping("/member/detail/{evaluationId}")
    public Evaluation getEvaluation(@PathVariable Long evaluationId, Authentication authentication) {
        return evaluationService.getEvaluation(evaluationId, authentication);
    }


    /* 평가 등록 */
    @PostMapping
    public Evaluation registerEvaluation(@RequestParam Long studyId, @RequestParam String targetId,
                                         @RequestParam double starRating, @RequestParam String reason,
                                         Authentication authentication) {
        return evaluationService.registerEvaluation(studyId, targetId, starRating, reason, authentication);
    }
}
