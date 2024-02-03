/*
package com.web.stard.service;

import com.web.stard.domain.*;
import com.web.stard.domain.board.study.dto.StudyDto;
import com.web.stard.domain.board.global.repository.PostRepository;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class ReplyServiceTest {

    @Autowired
    private MemberService memberService;
    @Autowired
    private ReplyService replyService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommunityService communityService;
    @Autowired
    private QnaService qnaService;
    @Autowired
    private StudyService studyService;

    //@Rollback(false)
    @Test
    void Post_COMM_댓글_등록() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);

        Post post = new Post();
        post.setTitle("community post 제목");
        post.setContent("community post 내용");
        Post createdPost = communityService.registerCommPost(post, authentication);

        Reply reply = new Reply();
        reply.setContent("community post에 대한 댓글");

        //when
        //replyService.createPostReply(post.getId(), reply.getContent(), authentication); // 작성자가 댓글 생성
        replyService.createPostReply(createdPost.getId(), reply.getContent(), authentication2);    // 다른 회원이 댓글 생성

        //then
        Page<Reply> savedRepliesPage = replyService.findAllReplies(1);
        List<Reply> savedReplies = savedRepliesPage.getContent();

        assertEquals(1, savedReplies.size()); // 생성된 댓글이 1개인지 확인

        Reply savedReply = savedReplies.get(0);

        assertEquals(savedReply.getPost().getId(), createdPost.getId()); // 댓글에 저장된 postId가 댓글 단 게시글의 아이디와 일치하는지 확인
        assertEquals(PostType.COMM, savedReply.getType()); // 댓글 단 게시글의 타입이 COMM인지 확인
    }

    //@Rollback(false)
    @Test
    void Post_QNA_댓글_등록() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);

        Post post = new Post();
        post.setTitle("qna post 제목");
        post.setContent("qna post 내용");
        Post createdPost = qnaService.createQna(post, authentication);

        Reply reply = new Reply();
        reply.setContent("qna post에 대한 댓글");

        //when
        //replyService.createPostReply(post.getId(), reply.getContent(), authentication); // 작성자가 댓글 생성
        replyService.createPostReply(createdPost.getId(), reply.getContent(), authentication2);    // 다른 회원이 댓글 생성

        //then
        Page<Reply> savedRepliesPage = replyService.findAllReplies(1);
        List<Reply> savedReplies = savedRepliesPage.getContent();

        assertEquals(1, savedReplies.size()); // 생성된 댓글이 1개인지 확인

        Reply savedReply = savedReplies.get(0);

        assertEquals(savedReply.getPost().getId(), createdPost.getId()); // 댓글에 저장된 postId가 댓글 단 게시글의 아이디와 일치하는지 확인
        assertEquals(PostType.QNA, savedReply.getType()); // 댓글 단 게시글의 타입이 QNA인지 확인
    }

    //@Rollback(false)
    @Test
    void Study_댓글_등록() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);

        StudyDto studyDto = new StudyDto("study 제목", "study 내용", 5, member.getId(),
                null, null, null, "online", LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),LocalDateTime.now().plusDays(7), RecruitStatus.RECRUITING, ProgressStatus.BEFORE_PROCEEDING, 0);
        Study createdStudy = studyService.createStudy(studyDto, authentication);

        Reply reply = new Reply();
        reply.setContent("study에 대한 댓글");

        //when
        replyService.createStudyReply(createdStudy.getId(), reply.getContent(), authentication); // 작성자가 댓글 생성
        //replyService.createStudyReply(createdStudy.getId(), reply.getContent(), authentication2);    // 다른 회원이 댓글 생성

        //then
        Page<Reply> savedRepliesPage = replyService.findAllReplies(1);
        List<Reply> savedReplies = savedRepliesPage.getContent();

        assertEquals(1, savedReplies.size()); // 생성된 댓글이 1개인지 확인

        Reply savedReply = savedReplies.get(0);

        if (reply.getId() != null && savedReply.getId() != null) {
            assertEquals(reply.getId(), savedReply.getId());
        }
        assertEquals(PostType.STUDY, savedReply.getType()); // 댓글 단 게시글의 타입이 STUDY인지 확인
    }

    //@Rollback(false)
    @Test
    void Post_댓글_수정_작성자() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        // 1. qna
//        Post post = new Post();
//        post.setTitle("qna post 제목");
//        post.setContent("qna post 내용");
//        Post createdPost = qnaService.createQna(post, authentication);

        // 2. comm
        Post post = new Post();
        post.setTitle("comm post 제목");
        post.setContent("comm post 내용");
        Post createdPost = communityService.registerCommPost(post, authentication);

        // 댓글 생성
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("원래 댓글 내용");
        Reply createdReply = replyService.createPostReply(createdPost.getId(), reply.getContent(), authentication);

        String updatedContent = "수정된 댓글 내용";

        //when
        Reply updatedReply = replyService.updateReply(createdReply.getId(), updatedContent, authentication);

        //then
        assertNotNull(updatedReply);
        assertEquals(updatedContent, updatedReply.getContent());    // 수정한 댓글이 반영됐는지
        assertEquals(member.getId(), updatedReply.getMember().getId()); // 작성자가 맞는지
    }

    //@Rollback(false)
    @Test
    void Study_댓글_수정_작성자() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        StudyDto studyDto = new StudyDto("study 제목", "study 내용", 5, member.getId(),
                null, null, null, "online", LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),LocalDateTime.now().plusDays(7), RecruitStatus.RECRUITING, ProgressStatus.BEFORE_PROCEEDING, 0);
        Study createdStudy = studyService.createStudy(studyDto, authentication);

        // 댓글 생성
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("원래 댓글 내용");
        Reply createdReply = replyService.createStudyReply(createdStudy.getId(), reply.getContent(), authentication);

        String updatedContent = "수정된 댓글 내용";

        //when
        Reply updatedReply = replyService.updateReply(createdReply.getId(), updatedContent, authentication);

        //then
        assertNotNull(updatedReply);
        assertEquals(updatedContent, updatedReply.getContent());    // 수정한 댓글이 반영됐는지
        assertEquals(member.getId(), updatedReply.getMember().getId()); // 작성자가 맞는지
    }

    //@Rollback(false)
    @Test
    void Post_댓글_수정_사용자_예외() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);

        // 1. qna
        Post post = new Post();
        post.setTitle("qna post 제목");
        post.setContent("qna post 내용");
        Post createdPost = qnaService.createQna(post, authentication);

        // 2. comm
//        Post post = new Post();
//        post.setTitle("comm post 제목");
//        post.setContent("comm post 내용");
//        Post createdPost = communityService.registerCommPost(post, authentication);

        // 댓글 생성
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("원래 댓글 내용");
        Reply createdReply = replyService.createPostReply(createdPost.getId(), reply.getContent(), authentication);

        String updatedContent = "수정된 댓글 내용";

        //when
        Reply updatedReply = replyService.updateReply(createdReply.getId(), updatedContent, authentication2);   // 작성자 아닌 회원이 수정

        //then
        assertNotNull(updatedReply);
        assertEquals(updatedContent, updatedReply.getContent());    // 수정한 댓글이 반영됐는지
        assertEquals(member.getId(), updatedReply.getMember().getId()); // 작성자가 맞는지(-> IllegalStateException 예외 발생)
    }

    //@Rollback(false)
    @Test
    void Study_댓글_수정_사용자_예외() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);

        StudyDto studyDto = new StudyDto("study 제목", "study 내용", 5, member.getId(),
                null, null, null, "online", LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),LocalDateTime.now().plusDays(7), RecruitStatus.RECRUITING, ProgressStatus.BEFORE_PROCEEDING, 0);
        Study createdStudy = studyService.createStudy(studyDto, authentication);

        // 댓글 생성
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("원래 댓글 내용");
        Reply createdReply = replyService.createStudyReply(createdStudy.getId(), reply.getContent(), authentication);

        String updatedContent = "수정된 댓글 내용";

        //when
        Reply updatedReply = replyService.updateReply(createdReply.getId(), updatedContent, authentication2);   // 작성자 아닌 회원이 수정

        //then
        assertNotNull(updatedReply);
        assertEquals(updatedContent, updatedReply.getContent());    // 수정한 댓글이 반영됐는지
        assertEquals(member.getId(), updatedReply.getMember().getId()); // 작성자가 맞는지(-> IllegalStateException 예외 발생)
    }

    //@Rollback(false)
    @Test
    void Post_댓글_삭제_작성자() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        // 1. qna
        Post post = new Post();
        post.setTitle("qna post 제목");
        post.setContent("qna post 내용");
        Post createdPost = qnaService.createQna(post, authentication);

        // 2. comm
//        Post post = new Post();
//        post.setTitle("comm post 제목");
//        post.setContent("comm post 내용");
//        Post createdPost = communityService.registerCommPost(post, authentication);

        // 댓글 생성
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("댓글 내용");
        Reply createdReply = replyService.createPostReply(createdPost.getId(), reply.getContent(), authentication);

        //when
        replyService.deleteReply(createdReply.getId(), authentication);

        //then
        Optional<Post> deletedPost = postRepository.findById(createdReply.getId());
        assertFalse(deletedPost.isPresent());   // 삭제한 댓글이 존재하는지 확인
    }

    //@Rollback(false)
    @Test
    void Study_댓글_삭제_작성자() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        StudyDto studyDto = new StudyDto("study 제목", "study 내용", 5, member.getId(),
                null, null, null, "online", LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),LocalDateTime.now().plusDays(7), RecruitStatus.RECRUITING, ProgressStatus.BEFORE_PROCEEDING, 0);
        Study createdStudy = studyService.createStudy(studyDto, authentication);

        // 댓글 생성
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("댓글 내용");
        Reply createdReply = replyService.createStudyReply(createdStudy.getId(), reply.getContent(), authentication);

        //when
        replyService.deleteReply(createdReply.getId(), authentication);

        //then
        Optional<Post> deletedPost = postRepository.findById(createdReply.getId());
        assertFalse(deletedPost.isPresent());   // 삭제한 댓글이 존재하는지 확인
    }

    //@Rollback(false)
    @Test
    void Post_댓글_삭제_사용자_예외() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);

        // 1. qna
        Post post = new Post();
        post.setTitle("qna post 제목");
        post.setContent("qna post 내용");
        Post createdPost = qnaService.createQna(post, authentication);

        // 2. comm
//        Post post = new Post();
//        post.setTitle("comm post 제목");
//        post.setContent("comm post 내용");
//        Post createdPost = communityService.registerCommPost(post, authentication);

        // 댓글 생성
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("댓글 내용");
        Reply createdReply = replyService.createPostReply(createdPost.getId(), reply.getContent(), authentication);

        //when
        replyService.deleteReply(createdReply.getId(), authentication2);

        //then
        Optional<Post> deletedPost = postRepository.findById(createdReply.getId());
        assertFalse(deletedPost.isPresent());   // 삭제한 댓글이 존재하는지 확인
    }

    //@Rollback(false)
    @Test
    void Study_댓글_삭제_사용자_예외() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Member member2 = new Member();
        member2.setId("testUser2");
        memberService.saveMember(member2);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);
        Authentication authentication2 = new UsernamePasswordAuthenticationToken(member2.getId(), null);

        StudyDto studyDto = new StudyDto("study 제목", "study 내용", 5, member.getId(),
                null, null, null, "online", LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),LocalDateTime.now().plusDays(7), RecruitStatus.RECRUITING, ProgressStatus.BEFORE_PROCEEDING, 0);
        Study createdStudy = studyService.createStudy(studyDto, authentication);

        // 댓글 생성
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("댓글 내용");
        Reply createdReply = replyService.createStudyReply(createdStudy.getId(), reply.getContent(), authentication);

        //when
        replyService.deleteReply(createdReply.getId(), authentication2);    // 작성자x

        //then
        Optional<Post> deletedPost = postRepository.findById(createdReply.getId());
        assertFalse(deletedPost.isPresent());   // 삭제한 댓글이 존재하는지 확인
    }

    //@Rollback(false)
    @Test
    void Post_댓글_삭제_관리자() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);
        Authentication adminAuth = new UsernamePasswordAuthenticationToken("testAdmin", null);

        // 1. qna
//        Post post = new Post();
//        post.setTitle("qna post 제목");
//        post.setContent("qna post 내용");
//        Post createdPost = qnaService.createQna(post, authentication);

        // 2. comm
        Post post = new Post();
        post.setTitle("comm post 제목");
        post.setContent("comm post 내용");
        Post createdPost = communityService.registerCommPost(post, authentication);

        // 댓글 생성
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("댓글 내용");
        Reply createdReply = replyService.createPostReply(createdPost.getId(), reply.getContent(), authentication);

        //when
        replyService.deleteReply(createdReply.getId(), adminAuth);  // 관리자가 삭제

        //then
        Optional<Post> deletedPost = postRepository.findById(createdReply.getId());
        assertFalse(deletedPost.isPresent());   // 삭제한 댓글이 존재하는지 확인
    }

    //@Rollback(false)
    @Test
    void Study_댓글_삭제_관리자() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);
        Authentication adminAuth = new UsernamePasswordAuthenticationToken("testAdmin", null);

        StudyDto studyDto = new StudyDto("study 제목", "study 내용", 5, member.getId(),
                null, null, null, "online", LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),LocalDateTime.now().plusDays(7), RecruitStatus.RECRUITING, ProgressStatus.BEFORE_PROCEEDING, 0);
        Study createdStudy = studyService.createStudy(studyDto, authentication);

        // 댓글 생성
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("댓글 내용");
        Reply createdReply = replyService.createStudyReply(createdStudy.getId(), reply.getContent(), authentication);

        //when
        replyService.deleteReply(createdReply.getId(), adminAuth);    // 관리자가 삭제

        //then
        Optional<Post> deletedPost = postRepository.findById(createdReply.getId());
        assertFalse(deletedPost.isPresent());   // 삭제한 댓글이 존재하는지 확인
    }

    //@Rollback(false)
    @Test
    void Post_댓글_조회() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        // 1. qna
//        Post post = new Post();
//        post.setTitle("qna post 제목");
//        post.setContent("qna post 내용");
//        Post createdPost = qnaService.createQna(post, authentication);

        // 2. comm
        Post post = new Post();
        post.setTitle("comm post 제목");
        post.setContent("comm post 내용");
        Post createdPost = communityService.registerCommPost(post, authentication);

        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("댓글");
        Reply createdReply = replyService.createPostReply(createdPost.getId(), reply.getContent(), authentication);

        //when
        Reply retrievedReply = replyService.getReply(createdReply.getId());

        //then
        assertNotNull(retrievedReply);
        assertEquals(createdReply.getId(), retrievedReply.getId());
        assertEquals(createdReply.getContent(), retrievedReply.getContent());
        assertEquals(createdReply.getMember().getId(), retrievedReply.getMember().getId());
//        assertEquals(PostType.QNA, retrievedReply.getType());
        assertEquals(PostType.COMM, retrievedReply.getType());
        assertEquals(createdReply.getType(), retrievedReply.getType());
    }

    //@Rollback(false)
    @Test
    void Study_댓글_조회() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        StudyDto studyDto = new StudyDto("study 제목", "study 내용", 5, member.getId(),
                null, null, null, "online", LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),LocalDateTime.now().plusDays(7), RecruitStatus.RECRUITING, ProgressStatus.BEFORE_PROCEEDING, 0);
        Study createdStudy = studyService.createStudy(studyDto, authentication);

        // 댓글 생성
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("댓글");
        Reply createdReply = replyService.createStudyReply(createdStudy.getId(), reply.getContent(), authentication);

        //when
        Reply retrievedReply = replyService.getReply(createdReply.getId());

        //then
        assertNotNull(retrievedReply);
        assertEquals(createdReply.getId(), retrievedReply.getId());
        assertEquals(createdReply.getContent(), retrievedReply.getContent());
        assertEquals(createdReply.getMember().getId(), retrievedReply.getMember().getId());
        assertEquals(PostType.STUDY, retrievedReply.getType());
        assertEquals(createdReply.getType(), retrievedReply.getType());
    }

    //@Rollback(false)
    @Test
    void 댓글_전체조회() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        // 게시글 생성
        Post post = new Post();
        post.setTitle("comm post 제목");
        post.setContent("comm post 내용");
        Post createdPost = communityService.registerCommPost(post, authentication);

        Post post2 = new Post();
        post2.setTitle("qna post 제목");
        post2.setContent("qna post 내용");
        Post createdPost2 = qnaService.createQna(post2, authentication);

        StudyDto studyDto = new StudyDto("study 제목", "study 내용", 5, member.getId(),
                null, null, null, "online", LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),LocalDateTime.now().plusDays(7), RecruitStatus.RECRUITING, ProgressStatus.BEFORE_PROCEEDING, 0);
        Study createdStudy = studyService.createStudy(studyDto, authentication);

        // 댓글 생성
        Reply createdCommReply = replyService.createPostReply(createdPost.getId(), "커뮤니티 댓글 내용", authentication);
        Reply createdQnaReply = replyService.createPostReply(createdPost2.getId(), "qna 댓글 내용", authentication);
        Reply createdStudyReply = replyService.createStudyReply(createdStudy.getId(), "스터디 댓글 내용", authentication);

        // When
        Page<Reply> savedRepliesPage = replyService.findAllReplies(1);
        List<Reply> savedReplies = savedRepliesPage.getContent();   // 페이지의 댓글 리스트 가져오기

        // Then
        assertEquals(3, savedReplies.size()); // 생성된 댓글 개수 확인
        // 댓글 리스트 최신 순으로 나오는지 확인
        assertEquals("스터디 댓글 내용", savedReplies.get(0).getContent());
        assertEquals("qna 댓글 내용", savedReplies.get(1).getContent());
        assertEquals("커뮤니티 댓글 내용", savedReplies.get(2).getContent());
    }

    //@Rollback(false)
    @Test
    void Post_아이디별_댓글조회() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        // 1. qna
        Post post = new Post();
        post.setTitle("qna post 제목");
        post.setContent("qna post 내용");
        Post createdPost = qnaService.createQna(post, authentication);

        // 2. comm
//        Post post = new Post();
//        post.setTitle("comm post 제목");
//        post.setContent("comm post 내용");
//        Post createdPost = communityService.registerCommPost(post, authentication);

        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("댓글 내용 1");
        Reply createdReply1 = replyService.createPostReply(createdPost.getId(), reply.getContent(), authentication);

        Reply reply2 = new Reply();
        reply2.setMember(member);
        reply2.setContent("댓글 내용 2");
        Reply createdReply2 = replyService.createPostReply(createdPost.getId(), reply2.getContent(), authentication);

        //when
        List<Reply> replies = replyService.findAllRepliesByPostIdOrderByCreatedAtAsc(createdPost.getId());

        //then
        assertEquals(2, replies.size()); // 생성된 댓글 개수 확인
        assertEquals(createdReply1.getContent(), replies.get(0).getContent()); // 댓글 내용 순서 확인
        assertEquals(createdReply2.getContent(), replies.get(1).getContent());
    }

    //@Rollback(false)
    @Test
    void Study_아이디별_댓글조회() {
        //given
        Member member = new Member();
        member.setId("testUser");
        memberService.saveMember(member);

        Authentication authentication = new UsernamePasswordAuthenticationToken(member.getId(), null);

        StudyDto studyDto = new StudyDto("study 제목", "study 내용", 5, member.getId(),
                null, null, null, "online", LocalDateTime.now().plusDays(7),
                LocalDateTime.now(),LocalDateTime.now().plusDays(7), RecruitStatus.RECRUITING, ProgressStatus.BEFORE_PROCEEDING, 0);
        Study createdStudy = studyService.createStudy(studyDto, authentication);

        // 댓글 생성
        Reply reply = new Reply();
        reply.setMember(member);
        reply.setContent("댓글 내용 1");
        Reply createdReply1 = replyService.createStudyReply(createdStudy.getId(), reply.getContent(), authentication);

        Reply reply2 = new Reply();
        reply2.setMember(member);
        reply2.setContent("댓글 내용 2");
        Reply createdReply2 = replyService.createStudyReply(createdStudy.getId(), reply2.getContent(), authentication);

        //when
        List<Reply> replies = replyService.findAllRepliesByStudyIdOrderByCreatedAtAsc(createdStudy.getId());

        //then
        assertEquals(2, replies.size()); // 생성된 댓글 개수 확인
        assertEquals(createdReply1.getContent(), replies.get(0).getContent()); // 댓글 내용 순서 확인
        assertEquals(createdReply2.getContent(), replies.get(1).getContent());
    }
}*/
