/*
package com.web.stard.service;

import com.web.stard.domain.*;
import com.web.stard.dto.StudyDto;
import com.web.stard.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class ReportServiceTest {

    @Autowired private ReportRepository reportRepository;
    @Autowired private ReportDetailRepository reportDetailRepository;
    @Autowired private MemberService memberService;
    @Autowired private StudyService studyService;
    @Autowired private PostRepository postRepository;
    @Autowired private CommunityService communityService;
    @Autowired private QnaService qnaService;
    @Autowired private ReplyService replyService;
    @Autowired private ReplyRepository replyRepository;
    @Autowired private ReportService reportService;

    //@Rollback(false)
    @Test
    void post글_신고() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        // 1. COMM post
*/
/*
        Post post = new Post();
        post.setTitle("community post 제목");
        post.setContent("community post 내용");
        post.setMember(member);
        Post createdPost = communityService.registerCommPost(post, authentication);
*//*


        // 2. QNA post
        Post post = new Post();
        post.setTitle("qna post 제목");
        post.setContent("qna post 내용");
        post.setMember(member);
        Post createdPost = qnaService.createQna(post, authentication);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);
*/
/*
        ReportReason reason = ReportReason.SPAM;
        String customReason = null;
*//*


        ReportReason reason = ReportReason.ETC;
        String customReason = "사용자 정의 사유";

        //when
        ReportDetail reportDetail = reportService.createPostReport(createdPost.getId(), reason, customReason, authentication2);  // member의 글을 member2가 신고

        //then
        assertNotNull(reportDetail);
        assertEquals(reason, reportDetail.getReason()); // 이유가 올바른지 확인
        //assertEquals(customReason, reportDetail.getCustomReason()); // 사용자 정의 사유가 올바른지 확인
        assertEquals("사용자 정의 사유", reportDetail.getCustomReason());
        assertEquals(member2.getId(), reportDetail.getMember().getId()); // 신고자가 올바른지 확인
    }


    //@Rollback(false)
    @Test
    void Reply_댓글신고() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        // QNA post
        Post post = new Post();
        post.setTitle("qna post 제목");
        post.setContent("qna post 내용");
        post.setMember(member);
        Post createdPost = qnaService.createQna(post, authentication);

        // 댓글
        Reply reply = new Reply();
        reply.setContent("qna post에 대한 댓글");
        Reply createdReply = replyService.createPostReply(createdPost.getId(), reply.getContent(), authentication);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);

        ReportReason reason = ReportReason.SPAM;
        String customReason = "";

*/
/*
        ReportReason reason = ReportReason.ETC;
        String customReason = "사용자 정의 사유";
*//*


        //when
        ReportDetail reportDetail = reportService.createReplyReport(createdReply.getId(), reason, customReason, authentication2);  // member의 댓글을 member2가 신고

        //then
        assertNotNull(reportDetail);
        assertEquals(reason, reportDetail.getReason()); // 이유가 올바른지 확인
        assertEquals(customReason, reportDetail.getCustomReason()); // 사용자 정의 사유가 올바른지 확인
        //assertEquals("사용자 정의 사유", reportDetail.getCustomReason());
        assertEquals(member2.getId(), reportDetail.getMember().getId()); // 신고자가 올바른지 확인
    }

    //@Rollback(false)
    @Test
    void Study글_신고() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        StudyDto studyDto = new StudyDto("study 제목", "study 내용", 5, member.getId(),
                null, null, null, "online", LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),LocalDateTime.now().plusDays(7), RecruitStatus.RECRUITING, ProgressStatus.BEFORE_PROCEEDING, 0);
        Study createdStudy = studyService.createStudy(studyDto, authentication);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);

*/
/*
        ReportReason reason = ReportReason.SPAM;
        String customReason = null;
*//*


        ReportReason reason = ReportReason.ETC;
        String customReason = "사용자 정의 사유";

        //when
        ReportDetail reportDetail = reportService.createStudyReport(createdStudy.getId(), reason, customReason, authentication2);  // member의 댓글을 member2가 신고

        //then
        assertNotNull(reportDetail);
        assertEquals(reason, reportDetail.getReason()); // 이유가 올바른지 확인
        //assertEquals(customReason, reportDetail.getCustomReason()); // 사용자 정의 사유가 올바른지 확인
        assertEquals("사용자 정의 사유", reportDetail.getCustomReason());
        assertEquals(member2.getId(), reportDetail.getMember().getId()); // 신고자가 올바른지 확인
    }

    //@Rollback(false)
    @Test
    void 자신의post글_신고_예외() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        Post post = new Post();
        post.setTitle("qna post 제목");
        post.setContent("qna post 내용");
        post.setMember(member);
        Post createdPost = qnaService.createQna(post, authentication);

        ReportReason reason = ReportReason.ETC;
        String customReason = "사용자 정의 사유";

        //when
        ReportDetail reportDetail = reportService.createPostReport(createdPost.getId(), reason, customReason, authentication);  // member가 자신의 글 신고

        //then
        assertNotNull(reportDetail);
        assertEquals(reason, reportDetail.getReason()); // 이유가 올바른지 확인
        assertEquals("사용자 정의 사유", reportDetail.getCustomReason());
        assertEquals(member.getId(), reportDetail.getMember().getId()); // 신고자가 올바른지 확인
    }

    //@Rollback(false)
    @Test
    void 신고횟수조회() {
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        // QNA post
        Post post = new Post();
        post.setTitle("qna post 제목");
        post.setContent("qna post 내용");
        post.setMember(member);
        Post createdPost = qnaService.createQna(post, authentication);

        // 댓글
        Reply reply = new Reply();
        reply.setContent("qna post에 대한 댓글");
        Reply createdReply = replyService.createPostReply(createdPost.getId(), reply.getContent(), authentication);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);

        Member member3 = new Member();
        member3.setId("testUser3");
        memberService.saveMember(member3);
        Authentication authentication3 = new UsernamePasswordAuthenticationToken(member3.getId(), null);

        ReportReason reason = ReportReason.SPAM;
        String customReason = null;

        ReportReason reason2 = ReportReason.ETC;
        String customReason2 = "사용자 정의 사유";

        ReportDetail reportDetail = reportService.createReplyReport(createdReply.getId(), reason, customReason, authentication2);  // member의 댓글을 member2가 신고
        ReportDetail reportDetail2 = reportService.createReplyReport(createdReply.getId(), reason2, customReason2, authentication3);  // member의 댓글을 member3이 신고

        //when
        Report report = reportService.getReportRepository().findByReplyId(createdReply.getId());    // 댓글id로 신고 조회
        long reportedCount = reportService.getReportCountForReport(report.getId()); // 신고id로 신고자 수 조회

        //then
        assertEquals(reportedCount, 2); // 이유가 올바른지 확인
    }

    //@Rollback(false)
    @Test
    void 신고목록조회() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        // QNA post
        Post post = new Post();
        post.setTitle("qna post 제목");
        post.setContent("qna post 내용");
        post.setMember(member);
        Post createdPost = qnaService.createQna(post, authentication);

        // 댓글
        Reply reply = new Reply();
        reply.setContent("qna post에 대한 댓글");
        Reply createdReply = replyService.createPostReply(createdPost.getId(), reply.getContent(), authentication);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);

        Member member3 = new Member();
        member3.setId("testUser3");
        memberService.saveMember(member3);
        Authentication authentication3 = new UsernamePasswordAuthenticationToken(member3.getId(), null);

        ReportReason reason = ReportReason.SPAM;
        String customReason = null;

        ReportReason reason2 = ReportReason.ETC;
        String customReason2 = "사용자 정의 사유";

        ReportDetail reportDetail = reportService.createReplyReport(createdReply.getId(), reason, customReason, authentication2);  // member의 댓글을 member2가 신고
        ReportDetail reportDetail2 = reportService.createReplyReport(createdReply.getId(), reason2, customReason2, authentication3);  // member의 댓글을 member3이 신고

        //when
        Report report = reportService.getReportRepository().findByReplyId(createdReply.getId());    // 댓글id로 신고 조회
        long reportedCount = reportService.getReportCountForReport(report.getId()); // 신고id로 신고자 수 조회

        //then
        assertEquals(reportedCount, 2); // 이유가 올바른지 확인
    }

    //@Rollback(false)
    @Test
    void 신고사유조회() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);

        Member member3 = new Member();
        member3.setId("testUser3");
        memberService.saveMember(member3);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);  // 글 작성자
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);    // 신고자1
        Authentication authentication3 = new UsernamePasswordAuthenticationToken(member3.getId(), null);    // 신고자2
        Authentication adminAuth = new UsernamePasswordAuthenticationToken("testAdmin", null);  // 관리자

        Post post = new Post();
        post.setTitle("qna post 제목");
        post.setContent("qna post 내용");
        post.setMember(member);
        Post createdPost = qnaService.createQna(post, authentication);

        ReportReason reason = ReportReason.SPAM;
        String customReason = null;

        ReportReason reason2 = ReportReason.ETC;
        String customReason2 = "사용자 정의 사유";

        // 신고
        ReportDetail reportDetail = reportService.createPostReport(createdPost.getId(), reason, customReason, authentication2);
        ReportDetail reportDetail2 = reportService.createPostReport(createdPost.getId(), reason2, customReason2, authentication3);

        Report report = reportDetail.getReport();

        //when
//        Set<String> reportReasons = reportService.getReportReasons(report.getId(), authentication1);  // 사용자가 조회 시 예외 발생

        Set<String> reportReasons = reportService.getReportReasons(report.getId(), adminAuth);

        // then
        assertEquals(2, reportReasons.size()); // 신고 이유가 2개 있는지 확인
        assertTrue(reportReasons.contains(reason.toString())); // 첫 번째 신고 이유가 신고 이유 목록에 있는지 확인
        assertTrue(reportReasons.contains(customReason2)); // 두 번째 신고 이유가 신고 이유 목록에 있는지 확인
    }

    //@Rollback(false)
    @Test
    void 신고반려() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);  // 글 작성자
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);    // 신고자1
        Authentication adminAuth = new UsernamePasswordAuthenticationToken("testAdmin", null);  // 관리자

        Post post = new Post();
        post.setTitle("qna post 제목");
        post.setContent("qna post 내용");
        post.setMember(member);
        Post createdPost = qnaService.createQna(post, authentication);

        ReportReason reason = ReportReason.SPAM;
        String customReason = null;

        ReportReason reason2 = ReportReason.ETC;
        String customReason2 = "사용자 정의 사유";

        // 신고
        ReportDetail reportDetail = reportService.createPostReport(createdPost.getId(), reason, customReason, authentication2);

        Report report = reportDetail.getReport();

        //when
        reportService.rejectReport(report.getId(), adminAuth);

        // then
        Report rejectedReport = reportService.getReportRepository().findByPostId(createdPost.getId());
        List<ReportDetail> rejectedReportDetail = reportDetailRepository.findByReportId(report.getId());

        assertNull(rejectedReport);  // 반려된 신고가 신고 내역에서 삭제되었는지 확인
        assertTrue(rejectedReportDetail.isEmpty());    // 신고자 내역도 삭제되었는지 확인
    }

    @Rollback(false)
    @Test
    void 신고승인() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);
        Member member3 = new Member();
        member3.setId("testUser3");
        memberService.saveMember(member3);
        Member member4 = new Member();
        member4.setId("testUser4");
        memberService.saveMember(member4);
        Member member5 = new Member();
        member5.setId("testUser5");
        memberService.saveMember(member5);
        Member member6 = new Member();
        member6.setId("testUser6");
        memberService.saveMember(member6);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);  // 글 작성자
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);    // 신고자2
        Authentication authentication3 = new UsernamePasswordAuthenticationToken(member3.getId(), null);    // 신고자3
        Authentication authentication4 = new UsernamePasswordAuthenticationToken(member4.getId(), null);    // 신고자4
        Authentication authentication5 = new UsernamePasswordAuthenticationToken(member5.getId(), null);    // 신고자5
        Authentication authentication6 = new UsernamePasswordAuthenticationToken(member6.getId(), null);    // 신고자6
        Authentication adminAuth = new UsernamePasswordAuthenticationToken("testAdmin", null);  // 관리자

        Post post = new Post();
        post.setTitle("qna post 제목");
        post.setContent("qna post 내용");
        post.setMember(member);
        Post createdPost = qnaService.createQna(post, authentication);

        Reply reply = new Reply();
        reply.setContent("qna post에 대한 댓글");
        Reply createdReply = replyService.createPostReply(createdPost.getId(), reply.getContent(), authentication);

        ReportReason reason = ReportReason.SPAM;
        String customReason = null;

        ReportReason reason2 = ReportReason.ETC;
        String customReason2 = "사용자 정의 사유";

        // 신고
        ReportDetail reportDetail = reportService.createPostReport(createdPost.getId(), reason, customReason, authentication2);
        ReportDetail reportDetail2 = reportService.createPostReport(createdPost.getId(), reason, customReason, authentication3);
        ReportDetail reportDetail3 = reportService.createPostReport(createdPost.getId(), reason, customReason, authentication4);
        ReportDetail reportDetail4 = reportService.createPostReport(createdPost.getId(), reason, customReason, authentication5);
        ReportDetail reportDetail5 = reportService.createPostReport(createdPost.getId(), reason, customReason, authentication6);

        Report report = reportDetail.getReport();

        //when
        reportService.acceptReport(report.getId(), adminAuth);

        // then
        Optional<Report> acceptedReport = reportService.getReportRepository().findById(report.getId());
        Page<Reply> replies = replyService.findAllReplies(1);
        List<ReportDetail> reportDetails = reportDetailRepository.findByReportId(report.getId());
        Member reportedMember = memberService.find(createdPost.getMember().getId());

        assertTrue(replies.isEmpty());      // 게시글에 달린 댓글까지 같이 삭제되었는지 확인
        assertFalse(postRepository.findById(createdPost.getId()).isPresent());  // 신고 처리된 글 삭제되었는지 확인
        assertFalse(acceptedReport.isPresent());  // 승인된 신고가 신고 내역에서 삭제되었는지 확인
        assertEquals(0, reportDetails.size());  // 신고자 내역도 삭제되었는지 확인
        assertEquals(1, reportedMember.getReportCount());   // 작성자에게 신고가 부여되었는지 확인

    }

    //@Rollback(false)
    @Test
    void 신고된회원_리스트조회() {
        //given
        Member member = new Member();
        member.setId("testUser");
        member.setReportCount(0);
        memberService.saveMember(member);

        Member member2 = new Member();
        member2.setId("testUser2");
        member2.setReportCount(4);
        memberService.saveMember(member2);

        Member member3 = new Member();
        member3.setId("testUser3");
        member3.setReportCount(5);
        memberService.saveMember(member3);

        Authentication adminAuth = new UsernamePasswordAuthenticationToken("testAdmin", null);  // 관리자

        //when
        List<Member> resultList = reportService.getMemberReports(adminAuth);

        //then
        assertEquals(2, resultList.size()); // 누적 신고 횟수가 1 이상인 회원이 2명인지 확인
    }

    //@Rollback(false)
    @Test
    void 강제탈퇴() {
        //given
        Member member = new Member();
        member.setId("testUser");
        member.setReportCount(0);
        memberService.saveMember(member);

        Member member2 = new Member();
        member2.setId("testUser2");
        member2.setReportCount(10);
        memberService.saveMember(member2);

        Member member3 = new Member();
        member3.setId("testUser3");
        member3.setReportCount(15);
        memberService.saveMember(member3);

        Authentication adminAuth = new UsernamePasswordAuthenticationToken("testAdmin", null);  // 관리자

        // When
        reportService.forceDeleteMember(member2.getId(), adminAuth);

        // Then
        Member deletedMember = memberService.find(member2.getId());
        assertNull(deletedMember); // 회원이 삭제되었는지 확인

        List<Post> userPosts = postRepository.findAllByMember(deletedMember);
        for (Post post : userPosts) {
            assertEquals("알수없음", post.getMember().getId()); // 강퇴 회원이 작성한 글의 작성자가 '알수없음'인지 확인
        }
    }

}*/
