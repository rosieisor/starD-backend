package com.web.stard.domain.board.global.application;

import com.web.stard.domain.board.community.application.CommunityService;
import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.global.domain.Reply;
import com.web.stard.domain.board.study.application.StudyPostService;
import com.web.stard.domain.board.study.application.StudyService;
import com.web.stard.domain.board.study.domain.Study;
import com.web.stard.domain.board.study.domain.StudyPost;
import com.web.stard.domain.board.study.repository.StudyRepository;
import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.member.domain.Role;
import com.web.stard.domain.board.global.repository.PostRepository;
import com.web.stard.domain.board.global.repository.ReplyRepository;
import com.web.stard.domain.board.study.repository.StudyPostRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
@RequiredArgsConstructor
@Getter @Setter
public class ReplyService {

    private final MemberService memberService;
    private final CommunityService communityService;
    private final PostRepository postRepository;
    private final ReplyRepository replyRepository;
    private final StudyService studyService;
    private final StudyRepository studyRepository;
    private final StudyPostService studyPostService;
    private final StudyPostRepository studyPostRepository;

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

    // StudyPost 댓글 생성
    public Reply createStudyPostReply(Long studyPostId, String replyContent, Authentication authentication) {
        String userId = authentication.getName();
        Member replier = memberService.find(userId);
        StudyPost targetStudyPost = studyPostService.getStudyPost(studyPostId, null);
        Study targetStudy = studyService.findById(targetStudyPost.getStudy().getId());

        Reply reply = Reply.builder()
                .member(replier)
                .studyPost(targetStudyPost)
                .study(targetStudy)
                .content(replyContent)
                .type(PostType.STUDYPOST)
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

    // studyPost 게시글 아이디 별 댓글 조회 (생성일 순)
    public List<Reply> findAllRepliesByStudyPostIdOrderByCreatedAtAsc(Long studyId) {
        return replyRepository.findAllByStudyPostIdOrderByCreatedAtAsc(studyId);
    }

    // 해당 id로 타입 조회 (댓글 작성 및 신고할 때 사용) - COMM, QNA, NOTICE, FAQ, STUDY, REPLY, STUDYPOST
    public PostType findPostTypeById(Long id) {
        // Post 조회
        Optional<Post> postOptional = postRepository.findById(id);
        // 해당 id가 post, study에 모두 존재하는 경우 구별하기 위해 고유한 필드값(notnull) 확인
        //TODO - id랑 작성자로 조회해야 할 것 같은데
        if (postOptional.isPresent() && postOptional.get().getCategory() != null) {
            return postOptional.get().getType();
        }

        // Study 조회
        Optional<Study> studyOptional = studyRepository.findById(id);
        if (studyOptional.isPresent() && studyOptional.get().getOnOff() != null) {
            return studyOptional.get().getType();
        }

        // Study Post 조회
        Optional<StudyPost> studyPostOptional = studyPostRepository.findById(id);
        if (studyPostOptional.isPresent() && studyPostOptional.get().getStudy() != null) {
            return studyPostOptional.get().getType();
        }

        // Reply 조회
/*        Optional<Reply> replyOptional = replyRepository.findById(id);
        if (replyOptional.isPresent() && replyOptional.get().getContent() != null) {
            return replyOptional.get().getType();
        }*/

        return PostType.REPLY;
    }

    /* 사용자가 작성한 댓글 조회(마이페이지) */
    public Page<Reply> findByMember(String memberId, int page) {
        Member member = memberService.find(memberId);

        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "createdAt"));
        Pageable pageable = PageRequest.of(page - 1, 10, sort);

        Page<Reply> replies = replyRepository.findByMember(member, pageable);

        return replies;
    }
}
