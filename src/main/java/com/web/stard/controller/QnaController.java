package com.web.stard.controller;

import com.web.stard.domain.Post;
import com.web.stard.service.MemberService;
import com.web.stard.service.QnaService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        String userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (!authentication.getName().equals("anonymousUser")) {
                userId = authentication.getName(); // 사용자 아이디
            }
        }
        return qnaService.getQnaDetail(id, userId);
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

    // faq, qna 최신 순 전체 보기
    @GetMapping("/all")
    public List<Post> findAllFaqAndQna() {
        return qnaService.getAllFaqsAndQnas();
    }

    // 전체 검색
    @GetMapping("/search")
    public List<Post> searchQnaAndFaq(@RequestParam String searchType, @RequestParam String searchWord) {
        return qnaService.searchQnaAndFaq(searchType, searchWord);
    }

    // 카테고리 - 전체 검색
    @GetMapping("/search/category")
    public List<Post> searchQnaOrFaqByCategory(@RequestParam String searchType, @RequestParam String category,
                                               @RequestParam String searchWord) {
        return qnaService.searchQnaOrFaqByCategory(searchType, category, searchWord);
    }
}
