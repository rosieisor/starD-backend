package com.web.stard.controller;

import com.web.stard.domain.Post;
import com.web.stard.service.MemberService;
import com.web.stard.service.QnaService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Getter @Setter
@RequestMapping("/qna")
@AllArgsConstructor
@RestController
public class QnaController {

    private final MemberService memberService;
    private final QnaService qnaService;

    // qna 등록
    @PostMapping
    public Post createQna(@RequestBody Post post, Authentication authentication) {
        qnaService.createQna(post, authentication);
        return post;
    }

    // qna 리스트 조회
    @GetMapping
    public List<Post> getAllQna(@RequestParam("page") int page) {
        return qnaService.getAllQna(page);
    }


    // qna 상세 조회
    @GetMapping("/{id}")
    public Post getQnaDetail(@PathVariable Long id ) {
        return qnaService.getQnaDetail(id);
    }

    // 수정
    @PostMapping("/{id}")
    public Post updateQna(@PathVariable Long id, @RequestBody Post requestPost, Authentication authentication) {
        Post post = qnaService.updateQna(id, requestPost, authentication);
        return post;
    }

    // qna 삭제
    @DeleteMapping("/{postId}")
    public void deleteQna(@PathVariable Long postId, Authentication authentication) {
        qnaService.deleteQna(postId, authentication);
    }
}
