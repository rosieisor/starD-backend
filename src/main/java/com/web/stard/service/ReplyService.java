package com.web.stard.service;

import com.web.stard.domain.*;
import com.web.stard.repository.PostRepository;
import com.web.stard.repository.ReplyRepository;
import com.web.stard.repository.StudyRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@Transactional
@Service
@AllArgsConstructor
@Getter @Setter
public class ReplyService {

    MemberService memberService;
    CommunityService communityService;
    PostRepository postRepository;
    ReplyRepository replyRepository;
    StudyService studyService;
    StudyRepository studyRepository;

    // 댓글이 존재하는지 확인
    private Reply getExistingReply(Long replyId) {
        Optional<Reply> optionalReply = replyRepository.findById(replyId);

        if (!optionalReply.isPresent()) {
            throw new IllegalArgumentException("해당 댓글을 찾을 수 없습니다.");
        }

        return optionalReply.get();
    }

    // 댓글 작성자인지 확인
    private void checkAuth(Reply reply, Member replier) {
        if (!reply.getMember().equals(replier)) {
            throw new IllegalStateException("댓글 작성자만 접근할 수 있습니다.");
        }
    }


    // Post(Community, Qna) 댓글 생성
    public Reply createPostReply(Long postId, String replyContent, Authentication authentication) {
        String userId = authentication.getName();
        Member replier = memberService.find(userId);

        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isEmpty()) {
            throw new EntityNotFoundException("게시물을 찾을 수 없습니다.");
        }

        Post targetPost = optionalPost.get();

        Reply reply = Reply.builder()
                .member(replier)
                .post(targetPost)
                .content(replyContent)
                .type(targetPost.getType())
                .build();

        return replyRepository.save(reply);
    }

    // Study 댓글 생성
    public Reply createStudyReply(Long studyId, String replyContent, Authentication authentication) {
        String userId = authentication.getName();
        Member replier = memberService.find(userId);
        Study targetStudy = studyService.findById(studyId);

        Reply reply = Reply.builder()
                .member(replier)
                .study(targetStudy)
                .content(replyContent)
                .type(PostType.STUDY)
                .build();

        return replyRepository.save(reply);
    }

    // 댓글 수정 (Post, Study 공통)
    public Reply updateReply(Long replyId, String replyContent, Authentication authentication) {
        String userId = authentication.getName();
        Member replier = memberService.find(userId);

        Reply reply = getExistingReply(replyId);

        checkAuth(reply, replier);

        reply.setContent(replyContent);
        return replyRepository.save(reply);
    }

    // 댓글 삭제 (Post, Study 공통)
    public void deleteReply(Long replyId, Authentication authentication) {
        String userId = authentication.getName();
        Member replier = memberService.find(userId);

        Reply reply = getExistingReply(replyId);

        if (replier.getRoles() != Role.ADMIN) {
            checkAuth(reply, replier);
        }

        replyRepository.delete(reply);
    }

    // 댓글 조회
    public Reply getReply(Long id) {
        Optional<Reply> reply = replyRepository.findById(id);
        if (reply.isPresent()) {
            return reply.get();
        }
        return null;
    }

    // 댓글 전체 조회 (최신순, 페이징)
    public Page<Reply> findAllReplies(int page) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page-1, 10, sort);
        return replyRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    // post 게시글 아이디 별 댓글 조회 (생성일 순)
    public List<Reply> findAllRepliesByPostIdOrderByCreatedAtAsc(Long postId) {
        return replyRepository.findAllByPostIdOrderByCreatedAtAsc(postId);
    }

    // study 게시글 아이디 별 댓글 조회 (생성일 순)
    public List<Reply> findAllRepliesByStudyIdOrderByCreatedAtAsc(Long studyId) {
        return replyRepository.findAllByStudyIdOrderByCreatedAtAsc(studyId);
    }

    // 댓글 작성하려는 게시글 타입 조회
    public PostType findPostTypeById(Long id) {
        // Post 조회
        Optional<Post> postOptional = postRepository.findById(id);
        // 해당 id가 post, study에 모두 존재하는 경우 구별하기 위해 고유한 필드값(notnull) 확인
        if (postOptional.isPresent() && postOptional.get().getCategory() != null) {
            return postOptional.get().getType();
        }

        // Study 조회
        Optional<Study> studyOptional = studyRepository.findById(id);
        if (studyOptional.isPresent() && studyOptional.get().getOnOff() != null) {
            return studyOptional.get().getType();
        }

        return null;
    }

}
