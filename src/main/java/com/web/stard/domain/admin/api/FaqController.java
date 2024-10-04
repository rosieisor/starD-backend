package com.web.stard.domain.admin.api;

import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.admin.application.FaqService;
import com.web.stard.domain.member.application.MemberService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.transaction.Transactional;

@Transactional
@Getter
@Setter
@RequestMapping("/faq")
@AllArgsConstructor
@RestController
public class FaqController {
    private final MemberService memberService;
    private  final FaqService faqService;

    // faq 등록
    @PostMapping
    public Post createFaq(@RequestBody Post post, Authentication authentication) {
        faqService.createFaq(post, authentication);
        return post;
    }

    // faq 리스트 조회(페이지화o)
    @GetMapping
    public Page<Post> getAllFaq(@RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return faqService.getAllFaq(page);
    }


    // faq 상세 조회
    @GetMapping("/{id}")
    public Post getFaqDetail(@PathVariable Long id) {
        String userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (!id.equals("anonymousUser")) {
                userId = authentication.getName(); // 사용자 아이디
            }
        }
        return faqService.getFaqDetail(id, userId);
    }

    // faq 수정
    @PostMapping("/{id}")
    public Post updateFaq(@PathVariable Long id, @RequestBody Post requestPost, Authentication authentication) {
        Post post = faqService.updateFaq(id, requestPost, authentication);
        return post;
    }

    // faq 삭제
    @DeleteMapping("/{id}")
    public void deleteFaq(@PathVariable Long id, Authentication authentication) {
        faqService.deleteFaq(id, authentication);
    }
}
