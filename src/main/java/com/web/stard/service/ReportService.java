package com.web.stard.service;

import com.web.stard.domain.*;
import com.web.stard.repository.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional
@Service
@AllArgsConstructor
@Getter
@Setter
public class ReportService {

    private ReportRepository reportRepository;
    private ReportDetailRepository reportDetailRepository;
    private MemberService memberService;
    private StudyRepository studyRepository;
    private PostRepository postRepository;
    private ReplyRepository replyRepository;
    private ReplyService replyService;
    private MemberRepository memberRepository;
    private StudyPostRepository studyPostRepository;
    private StarScrapService starScrapService;

    // 해당 글이 이미 신고되었는지 확인
    private Report isTargetPostAlreadyReported(Long targetId, PostType postType) {
        Report report = null;

        if (postType == PostType.COMM || postType == PostType.QNA) {
            report = reportRepository.findByPostId(targetId);
        }
        else if (postType == PostType.STUDY) {
            report = reportRepository.findByStudyId(targetId);
        }
        else if (postType == PostType.REPLY) {
            report = reportRepository.findByReplyId(targetId);
        }
        else if (postType == PostType.STUDYPOST) {
            report = reportRepository.findByStudyPostId(targetId);
        }

        return report;
    }

    // 회원이 이미 신고했는지 확인
    private boolean isUserAlreadyReported(Report report, Member currentUser) {
        ReportDetail reportDetail = null;
        if (report != null) {   // 신고 내역 존재
            reportDetail = reportDetailRepository.findByReportAndMember(report, currentUser);
            if (reportDetail != null) { // 회원이 신고
                return true;
            }
        }
        return false;
    }

    // 신고 생성 메서드
    private ReportDetail createReportDetail(Report report, ReportReason reason, String customReason, Member currentUser) {
        ReportDetail reportDetail = ReportDetail.builder()
                .report(report)
                .member(currentUser)
                .reason(reason)
                .customReason(customReason)
                .build();

        return reportDetailRepository.save(reportDetail);
    }

    // Post 게시글 신고 (COMM/QNA)
    public ReportDetail createPostReport(Long postId, ReportReason reason, String customReason, Authentication authentication) {
        String userId = authentication.getName();
        Member currentUser = memberService.find(userId);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("해당 post 게시글을 찾을 수 없습니다."));

        if (post.getMember() == currentUser) {
            throw new IllegalArgumentException("내가 작성한 글은 신고할 수 없습니다.");
        }

        Report existingReport = isTargetPostAlreadyReported(postId, post.getType());

        if (isUserAlreadyReported(existingReport, currentUser)) {
            throw new IllegalArgumentException("이미 신고한 post 게시글입니다.");
        }

        // 신고 내역이 있는 경우 - 신고자 정보 추가
        if (existingReport != null) {
            return createReportDetail(existingReport, reason, customReason, currentUser);
        } else {
            // 신고 내역이 없는 경우 - 신고 내역에 추가, 신고자 정보 추가
            Report report = Report.builder()
                    .post(post)
                    .tableType(post.getType())
                    .build();

            reportRepository.save(report);

            return createReportDetail(report, reason, customReason, currentUser);
        }
    }

    // Study 게시글 신고
    public ReportDetail createStudyReport(Long studyId, ReportReason reason, String customReason, Authentication authentication) {
        String userId = authentication.getName();
        Member currentUser = memberService.find(userId);

        Study study = studyRepository.findById(studyId)
                .orElseThrow(() -> new EntityNotFoundException("해당 study 게시글을 찾을 수 없습니다."));

        if (study.getRecruiter() == currentUser) {
            throw new IllegalArgumentException("내가 작성한 글은 신고할 수 없습니다.");
        }

        // 모집 중인 스터디만 신고 가능
        if (study.getRecruitStatus() != RecruitStatus.RECRUITING) {
            throw new IllegalArgumentException("모집 중인 스터디만 신고 가능합니다.");
        }

        Report existingReport = isTargetPostAlreadyReported(studyId, PostType.STUDY);

        if (isUserAlreadyReported(existingReport, currentUser)) {
            throw new IllegalArgumentException("이미 신고한 study 게시글입니다.");
        }

        // 신고 내역이 있는 경우 - 신고자 정보 추가
        if (existingReport != null) {
            return createReportDetail(existingReport, reason, customReason, currentUser);
        }
        else {
            // 신고 내역이 없는 경우 - 신고 내역에 추가, 신고자 정보 추가
            Report report = Report.builder()
                    .study(study)
                    .tableType(PostType.STUDY)
                    .build();

            reportRepository.save(report);

            return createReportDetail(report, reason, customReason, currentUser);
        }
    }

    // StudyPost 게시글 신고
    public ReportDetail createStudyPostReport(Long studyPostId, ReportReason reason, String customReason, Authentication authentication) {
        String userId = authentication.getName();
        Member currentUser = memberService.find(userId);

        StudyPost studyPost = studyPostRepository.findById(studyPostId)
                .orElseThrow(() -> new EntityNotFoundException("해당 post 게시글을 찾을 수 없습니다."));

        if (studyPost.getMember() == currentUser) {
            throw new IllegalArgumentException("내가 작성한 글은 신고할 수 없습니다.");
        }

        Report existingReport = isTargetPostAlreadyReported(studyPostId, studyPost.getType());

        if (isUserAlreadyReported(existingReport, currentUser)) {
            throw new IllegalArgumentException("이미 신고한 post 게시글입니다.");
        }

        // 신고 내역이 있는 경우 - 신고자 정보 추가
        if (existingReport != null) {
            return createReportDetail(existingReport, reason, customReason, currentUser);
        } else {
            // 신고 내역이 없는 경우 - 신고 내역에 추가, 신고자 정보 추가
            Report report = Report.builder()
                    .studyPost(studyPost)
                    .tableType(studyPost.getType())
                    .build();

            reportRepository.save(report);

            return createReportDetail(report, reason, customReason, currentUser);
        }
    }

    // 댓글 신고
    public ReportDetail createReplyReport(Long replyId, ReportReason reason, String customReason, Authentication authentication) {
        String userId = authentication.getName();
        Member currentUser = memberService.find(userId);

        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글을 찾을 수 없습니다."));

        if (reply.getMember() == currentUser) {
            throw new IllegalArgumentException("내가 작성한 댓글은 신고할 수 없습니다.");
        }
        
        Report existingReport = isTargetPostAlreadyReported(replyId, PostType.REPLY);

        if (isUserAlreadyReported(existingReport, currentUser)) {
            throw new IllegalArgumentException("이미 신고한 댓글입니다.");
        }

        // 신고 내역이 있는 경우 - 신고자 정보 추가
        if (existingReport != null) {
            return createReportDetail(existingReport, reason, customReason, currentUser);
        }
        else {
            // 신고 내역이 없는 경우 - 신고 내역에 추가, 신고자 정보 추가
            Report report = Report.builder()
                    .reply(reply)
                    .tableType(PostType.REPLY)
                    .build();

            reportRepository.save(report);

            return createReportDetail(report, reason, customReason, currentUser);
        }
    }

    // 관리자 여부 확인
    public void checkIfMemberIsAdmin(Authentication authentication) {
        String userId = authentication.getName();
        Role userRole = memberService.find(userId).getRoles();

        if (userRole != Role.ADMIN) {
            throw new AccessDeniedException("관리자가 아닙니다.");
        }
    }

    // 특정 report의 신고 횟수 조회
    public Long getReportCountForReport(Long reportId) {
        return reportDetailRepository.countByReportId(reportId);
    }

    // 신고 목록 조회 (누적 신고 수가 5회 이상인)
    public List<Report> getReports(Authentication authentication) {
        checkIfMemberIsAdmin(authentication);

        List<Report> reports = reportRepository.findAll();
        List<Report> resultList = new ArrayList<>();

        for (Report report : reports) {
            Long reportCount = getReportCountForReport(report.getId());

            // TODO 1->5로 수정하기
            if (reportCount >= 1) {
                resultList.add(report);
            }
        }

        return resultList;
    }

    // 특정 report의 신고 사유 조회
    public Set<String> getReportReasons(Long reportId, Authentication authentication) {
        checkIfMemberIsAdmin(authentication);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신고를 찾을 수 없습니다."));

        List<ReportDetail> reportDetails = reportDetailRepository.findByReportId(reportId);

        Set<String> reasons = new HashSet<>();

        for (ReportDetail reportDetail : reportDetails) {
            ReportReason reason = reportDetail.getReason();
            if (reason == ReportReason.ETC) {
                String customReason = reportDetail.getCustomReason();
                if (customReason != null && !customReason.isEmpty()) {
                    reasons.add(customReason);
                }
            } else {
                reasons.add(reason.name());
            }
        }

        return reasons;
    }

    // 신고 반려
    public void rejectReport(Long reportId, Authentication authentication) {
        checkIfMemberIsAdmin(authentication);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신고를 찾을 수 없습니다."));

        // reportId에 해당하는 ReportDetail 데이터 삭제
        List<ReportDetail> reportDetails = reportDetailRepository.findByReportId(reportId);
        reportDetailRepository.deleteAll(reportDetails);

        // 해당 신고 내역 (Report) 삭제
        reportRepository.delete(report);
    }

    // 신고 승인
    public void acceptReport(Long reportId, Authentication authentication) {
        checkIfMemberIsAdmin(authentication);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신고를 찾을 수 없습니다."));

        Member reporterMember = null;

        if (report.getTableType() == PostType.COMM || report.getTableType() == PostType.QNA) {
            reporterMember = report.getPost().getMember();
        }
        else if (report.getTableType() == PostType.REPLY) {
            reporterMember = report.getReply().getMember();
        }
        else if (report.getTableType() == PostType.STUDY) {
            reporterMember = report.getStudy().getRecruiter();
        }
        else if (report.getTableType() == PostType.STUDYPOST) {
            reporterMember = report.getStudyPost().getMember();
        }

        List<ReportDetail> reportDetails = reportDetailRepository.findByReportId(reportId);

        // 신고 승인된 글의 작성자에게 신고 부여
        // TODO 5로 변경하기
        if (reportDetails.size() >= 1) {
            reporterMember.setReportCount(reporterMember.getReportCount() + 1);
        }

        // 신고자 정보 삭제
        reportDetailRepository.deleteAll(reportDetails);

        // 신고 내역 삭제
        reportRepository.delete(report);

        // 신고 승인된 글, 댓글 삭제
        if (report.getTableType() == PostType.COMM || report.getTableType() == PostType.QNA) {
            Post post = report.getPost();
            List<Reply> replies = replyService.findAllRepliesByPostIdOrderByCreatedAtAsc(post.getId());

            if (replies != null) {
                replyRepository.deleteAll(replies);
            }
            starScrapService.deleteByPostId(post.getId());
            postRepository.deleteById(report.getPost().getId());
        }
        else if (report.getTableType() == PostType.REPLY) {
            replyRepository.deleteById(report.getReply().getId());
        }
        else if (report.getTableType() == PostType.STUDY) {
            Study study = report.getStudy();
            List<Reply> replies = replyService.findAllRepliesByStudyIdOrderByCreatedAtAsc(study.getId());

            if (replies != null) {
                replyRepository.deleteAll(replies);
            }
            starScrapService.deleteByStudyId(study.getId());
            studyRepository.deleteById(report.getStudy().getId());
        }
        else if (report.getTableType() == PostType.STUDYPOST) {
            StudyPost studyPost = report.getStudyPost();
            List<Reply> replies = replyService.findAllRepliesByStudyPostIdOrderByCreatedAtAsc(studyPost.getId());

            if (replies != null) {
                replyRepository.deleteAll(replies);
            }
            starScrapService.deleteByStudyPostId(studyPost.getId());
            studyPostRepository.deleteById(report.getStudyPost().getId());
        }

    }

    // 회원 목록 조회 (누적 신고 횟수가 1 이상인)
    public List<Member> getMemberReports(Authentication authentication) {
        checkIfMemberIsAdmin(authentication);

        List<Member> members = memberRepository.findAll();
        List<Member> resultList = new ArrayList<>();

        for (Member member : members) {
            if (member.getReportCount() >= 1) {
                resultList.add(member);
            }
        }

        return resultList;
    }

    // 강제 탈퇴
//    public void forceDeleteMember(String memberId, Authentication authentication) {
//        checkIfMemberIsAdmin(authentication);
//
//        Member member = memberService.find(memberId);
//
//        if (member.getReportCount() >= 10) {
//            // 해당 회원이 작성한 글, 댓글의 작성자는 '알 수 없음'으로 변경
//            Member removedMember = new Member();
//            removedMember.setId("알수없음");
//            removedMember.setNickname("알수없음");
//
//            List<Post> posts = postRepository.findAllByMember(member);
//            for (Post post : posts) {
//                post.setMember(removedMember);
//            }
//
//            // TODO - 스터디에 게시글 작성자를 저장할 수 있어야 구현 가능
///*
//            List<Study> studies = studyRepository.findAllByMember(member);
//            for (Study study : studies) {
//                study.setMember(removedMember);
//            }
//*/
//
//            List<Reply> replies = replyRepository.findAllByMember(member);
//            for (Reply reply : replies) {
//                reply.setMember(removedMember);
//            }
//
//            memberRepository.delete(member);
//        }
//    }

}