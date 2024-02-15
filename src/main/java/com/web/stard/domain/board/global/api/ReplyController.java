package com.web.stard.domain.board.global.api;

import com.web.stard.domain.board.global.domain.enums.PostType;
import com.web.stard.domain.board.global.domain.Reply;
import com.web.stard.domain.board.global.application.ReplyService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@RestController
@RequestMapping("/replies")
public class ReplyController {

    private final ReplyService replyService;

    // Post(Community, Qna) 댓글 생성
    @PostMapping("/post")
    public Reply createPostReply(@RequestBody Map<String, Object> requestPayload, Authentication authentication) {
        String targetIdStr = (String) requestPayload.get("targetId");
        Integer targetId = Integer.parseInt(targetIdStr);

        String replyContent = (String) requestPayload.get("replyContent");

        Long targetIdLong = targetId.longValue();

        return replyService.createPostReply(targetIdLong, replyContent, authentication);
    }

    // Study 댓글 생성
    @PostMapping("/study")
    public Reply createStudyReply(@RequestBody Map<String, Object> requestPayload, Authentication authentication) {
        // Request Body에서 필요한 데이터 추출
        //Integer targetId = (Integer) requestPayload.get("targetId"); // Integer로 변경
        String targetIdStr = (String) requestPayload.get("targetId");
        Integer targetId = Integer.parseInt(targetIdStr);

        String replyContent = (String) requestPayload.get("replyContent");

        Long targetIdLong = targetId.longValue(); // Integer를 Long으로 변환

        return replyService.createStudyReply(targetIdLong, replyContent, authentication);
    }

    // StudyPost 댓글 생성
    @PostMapping("/studypost")
    public Reply createStudyPostReply(@RequestBody Map<String, Object> requestPayload, Authentication authentication) {
        String targetIdStr = (String) requestPayload.get("targetId");
        Integer targetId = Integer.parseInt(targetIdStr);

        String replyContent = (String) requestPayload.get("replyContent");

        Long targetIdLong = targetId.longValue(); // Integer를 Long으로 변환

        return replyService.createStudyPostReply(targetIdLong, replyContent, authentication);
    }

    // 댓글 수정 (Post, Study 공통)
    @PostMapping("/{commentId}")
    public Reply updateReply(@PathVariable Long commentId, @RequestBody Map<String, String> requestMap, Authentication authentication) {
        String replyContent = requestMap.get("replyContent");
        return replyService.updateReply(commentId, replyContent, authentication);
    }

    // 댓글 삭제 (Post, Study 공통)
    @DeleteMapping("/{commentId}")
    public void deleteReply(@PathVariable Long commentId, Authentication authentication) {
        replyService.deleteReply(commentId, authentication);
    }

    // 댓글 조회
    @GetMapping("/{id}")
    public Reply getReply(@PathVariable Long id){
        return replyService.getReply(id);
    }

    // 댓글 전체 조회 (최신순, 페이징)
    @GetMapping()
    public Page<Reply> findAllReplies(@RequestParam("page") int page) {
        return replyService.findAllReplies(page);
    }

    // post 게시글 아이디 별 댓글 조회 (생성일 순)
    @GetMapping("/post/{targetId}")
    public List<Reply> findAllRepliesByPostId(@PathVariable Long targetId) {
        return replyService.findAllRepliesByPostIdOrderByCreatedAtAsc(targetId);
    }

    // study 게시글 아이디 별 댓글 조회 (생성일 순)
    @GetMapping("/study/{targetId}")
    public List<Reply> findAllRepliesByStudyId(@PathVariable Long targetId) {
        return replyService.findAllByStudyIdAndStudyPostIdIsNullOrderByCreatedAtAsc(targetId);
    }

    // studyPost 게시글 아이디 별 댓글 조회 (생성일 순)
    @GetMapping("/studypost/{targetId}")
    public List<Reply> findAllRepliesByStudyPostId(@PathVariable Long targetId) {
        return replyService.findAllRepliesByStudyPostIdOrderByCreatedAtAsc(targetId);
    }

    // 댓글 작성하려는 게시글의 타입 조회
    @GetMapping("/type/{targetId}")
    public PostType findPostTypeById(@PathVariable Long targetId) {
        return replyService.findPostTypeById(targetId);
    }

}
