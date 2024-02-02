package com.web.stard.service;

import com.web.stard.chat_stomp.ChatMessage;
import com.web.stard.chat_stomp.ChatMessageRepository;
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
import java.util.*;

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
    private InterestRepository interestRepository;
    private StudyMemberRepository studyMemberRepository;
    private ProfileRepository profileRepository;
    private ApplicantRepository applicantRepository;
    private AssigneeRepository assigneeRepository;
    private ChatMessageRepository chatMessageRepository;
    private EvaluationRepository evaluationRepository;
    private StarScrapRepository starScrapRepository;

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
                    .member(post.getMember())
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
    public Map<String, Integer> getReportReasons(Long reportId, Authentication authentication) {
        checkIfMemberIsAdmin(authentication);

        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("해당 신고를 찾을 수 없습니다."));

        List<ReportDetail> reportDetails = reportDetailRepository.findByReportId(reportId);

        Map<String, Integer> reasonsWithCount = new HashMap<>();

        for (ReportDetail reportDetail : reportDetails) {
            ReportReason reason = reportDetail.getReason();
            String reasonKey = null;

            if (reason == ReportReason.ETC) {
                String customReason = reportDetail.getCustomReason();
                if (customReason != null && !customReason.isEmpty()) {
                    reasonKey = customReason;
                }
            } else {
                reasonKey = reason.name();
            }

            // Map에서 특정 신고 사유에 1을 더한 후 다시 저장
            reasonsWithCount.put(reasonKey, reasonsWithCount.getOrDefault(reasonKey, 0) + 1);
        }

        return reasonsWithCount;
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

    // 강제 탈퇴 - 스케줄러
    @Transactional
    public void forceDeleteMember() {
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            if (member.getReportCount() >= 10) {
                // 탈퇴할 회원의 글과 댓글에 대한 Report 가져오기
                List<Report> deleteReports = reportRepository.findByMember(member);

                // deleteReports에 해당하는 reportDetail 삭제
                if (deleteReports != null) {
                    for (Report report : deleteReports) {
                        List<ReportDetail> reportDetails = reportDetailRepository.findByReportId(report.getId());
                        if (!reportDetails.isEmpty()) {
                            reportDetailRepository.deleteAll(reportDetails);
                        }
                    }

                    // deleteReports 삭제
                    reportRepository.deleteAll(deleteReports);
                }

                // 해당 회원의 공감, 스크랩 내역 삭제
                starScrapRepository.deleteByMember(member);

                // recruiter + progressStatus가 null인 것들만 삭제 (진행으로 넘어가지 않은 모집 게시글만 삭제되게)
                List<Study> deleteStudies = studyRepository.findStudiesByRecruiterAndNullProgressStatus(member);
                studyRepository.deleteAll(deleteStudies);

                // '알 수 없음' 변경
                Member updateMember = memberService.find("admin2");

                List<Applicant> updateApplicant = applicantRepository.findByMemberAndStudyProgressStatusIsNotNull(member);
                for (Applicant applicant : updateApplicant) {
                    applicant.setMember(updateMember);
                    applicantRepository.save(applicant);
                }

                List<Assignee> updateAssignee = assigneeRepository.findAllByMember(member);
                for (Assignee assignee : updateAssignee) {
                    Assignee newAssignee = new Assignee();
                    newAssignee.setToDo(assignee.getToDo());
                    newAssignee.setMember(updateMember);
                    newAssignee.setToDoStatus(assignee.isToDoStatus());

                    assigneeRepository.delete(assignee);
                    assigneeRepository.save(newAssignee);
                }

                List<ChatMessage> updateChatMessage = chatMessageRepository.findByMember(member);
                for (ChatMessage chatMessage : updateChatMessage) {
                    chatMessage.setMember(updateMember);
                    chatMessageRepository.save(chatMessage);
                }

                List<Evaluation> updateEvaluation = evaluationRepository.findByTargetOrderByStudyActivityDeadlineDesc(member); // 내가 target
                List<Evaluation> updateEvaluation2 = evaluationRepository.findByMemberOrderByStudyActivityDeadlineDesc(member); // 내가 member
                for (Evaluation evaluation : updateEvaluation) {
                    evaluation.setTarget(updateMember);
                    evaluationRepository.save(evaluation);
                }
                for (Evaluation evaluation : updateEvaluation2) {
                    evaluation.setMember(updateMember);
                    evaluationRepository.save(evaluation);
                }

                List<Post> updatePost = postRepository.findAllByMember(member);
                for (Post post : updatePost) {
                    post.setMember(updateMember);
                    postRepository.save(post);
                }

                List<Reply> updateReply = replyRepository.findAllByMember(member);
                for (Reply reply : updateReply) {
                    reply.setMember(updateMember);
                    replyRepository.save(reply);
                }

                List<Study> updateStudy = studyRepository.findByRecruiterAndProgressStatus(member, ProgressStatus.WRAP_UP);
                for (Study study : updateStudy) {
                    study.setRecruiter(updateMember);
                    studyRepository.save(study);
                }

                List<StudyMember> updateStudyMember = studyMemberRepository.findByMemberAndStudyProgressStatusOrderByIdDesc(member, ProgressStatus.WRAP_UP);
                for (StudyMember studyMember : updateStudyMember) {
                    studyMember.setMember(updateMember);
                    studyMemberRepository.save(studyMember);
                }

                List<StudyPost> updateStudyPost = studyPostRepository.findByMemberAndStudyProgressStatus(member, ProgressStatus.WRAP_UP);
                for (StudyPost studyPost : updateStudyPost) {
                    studyPost.setMember(updateMember);
                    studyPostRepository.save(studyPost);
                }

                interestRepository.deleteAllByMember(member);

                memberRepository.delete(member);

                // 프로필 삭제
                profileRepository.deleteById(member.getProfile().getId());
            }
        }
    }

}