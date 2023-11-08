package com.web.stard.controller;

import com.web.stard.domain.Member;
import com.web.stard.domain.Report;
import com.web.stard.domain.ReportDetail;
import com.web.stard.domain.ReportReason;
import com.web.stard.service.ReportService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService reportService;

    // Post 글 신고
    @PostMapping("/posts")
    public ReportDetail createPostReport(@RequestParam Long postId, @RequestParam ReportReason reason, @RequestParam(required = false) String customReason, Authentication authentication) {
        return reportService.createPostReport(postId, reason, customReason, authentication);
    }

    // Study 글 신고
    @PostMapping("/studies")
    public ReportDetail createStudyReport(@RequestParam Long studyId, @RequestParam ReportReason reason, @RequestParam(required = false) String customReason, Authentication authentication) {
        return reportService.createStudyReport(studyId, reason, customReason, authentication);
    }

    // 댓글 신고
    @PostMapping("/replies")
    public ReportDetail createReplyReport(@RequestParam Long replyId, @RequestParam ReportReason reason, @RequestParam(required = false) String customReason, Authentication authentication) {
        return reportService.createReplyReport(replyId, reason, customReason, authentication);
    }

    // 신고 목록 조회 (누적 신고 수가 5회 이상인)
    @GetMapping()
    public List<Report> getReports(Authentication authentication) {
        return reportService.getReports(authentication);
    }

    // 특정 report의 신고 사유 조회
    @GetMapping("/reason/{reportId}")
    public Set<String> getReportReasons(@PathVariable Long reportId, Authentication authentication) {
        return reportService.getReportReasons(reportId, authentication);
    }

    // 신고 반려
    @DeleteMapping("/{reportId}")
    public void rejectReport(@PathVariable Long reportId, Authentication authentication) {
        reportService.rejectReport(reportId, authentication);
    }

    // 신고 승인
    @PostMapping("/accept/{reportId}")
    public void acceptReport(@PathVariable Long reportId, Authentication authentication) {
        reportService.acceptReport(reportId, authentication);
    }

    @GetMapping("/members")
    // 회원 목록 조회 (누적 신고 횟수가 1 이상인)
    public List<Member> getMemberReports(Authentication authentication) {
        return reportService.getMemberReports(authentication);
    }

    @DeleteMapping("/members/{memberId}")
    // 강제 탈퇴
    public void forceDeleteMember(@PathVariable String memberId, Authentication authentication) {
        reportService.forceDeleteMember(memberId, authentication);
    }

}