package com.web.stard.controller;

import com.web.stard.domain.Post;
import com.web.stard.service.CommunityService;
import com.web.stard.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@RestController
@RequestMapping("/com")
public class CommunityController {

    private final MemberService memberService;
    private final CommunityService comService;

    /* 커뮤니티 게시글 조회 (페이지화 추가) */
    @GetMapping
    public List<Post> getAllCommunityPost(@RequestParam("page") int page) {
//        return comService.getAllCommunityPost(); // 페이지화 X (그냥 전체 조회)
        return comService.getAllCommunityPost(page);
    }

    /* 커뮤니티 게시글 세부 조회 */
    @GetMapping("/{id}")
    public Post getCommunityPost(@PathVariable Long id) {
        return comService.getCommunityPost(id);
    }

    /* 커뮤니티 게시글 등록 */
    @PostMapping
    public Post registerCommPost(@RequestBody Post requestPost, Authentication authentication) {
        Post post = comService.registerCommPost(requestPost, authentication);
        return post;
    }

    /* 커뮤니티 게시글 수정 */
    @PostMapping("/{id}")
    public Post updateCommPost(@PathVariable Long id, @RequestBody Post requestPost, Authentication authentication) {
        Post post = comService.updateCommPost(id, requestPost, authentication);
        return post;
    }

    /* 커뮤니티 게시글 삭제 */
    @DeleteMapping("/{id}")
    public boolean deleteCommPost(@PathVariable Long id, Authentication authentication) {
        return comService.deleteCommPost(id, authentication);
    }
}
