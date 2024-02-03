package com.web.stard.domain.board.community.api;

import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.board.community.dto.CommPostRequestDto;
import com.web.stard.domain.board.community.application.CommunityService;
import com.web.stard.domain.member.application.MemberService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Getter @Setter
@AllArgsConstructor
@RestController
@RequestMapping("/com")
public class CommunityController {

    private final MemberService memberService;
    private final CommunityService comService;

    /* 커뮤니티 게시글 조회 (페이지화 X) */
//    @GetMapping
//    public List<Post> getAllCommunityPost() {
//        return comService.getAllCommunityPost(); // 페이지화 X (그냥 전체 조회)
//    }

    /* 커뮤니티 게시글 조회 (페이지화 추가) */
    @GetMapping
    public Page<Post> getAllCommunityPost(@RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return comService.getAllCommunityPost(page);
    }

    /* 커뮤니티 게시글 세부 조회 */
    @GetMapping("/{id}")
    public Post getCommunityPost(@PathVariable Long id) {
        String userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getName());
        if (authentication != null && authentication.isAuthenticated()) {
            if (!authentication.getName().equals("anonymousUser")) {
                userId = authentication.getName(); // 사용자 아이디
            }
        }
        return comService.getCommunityPost(id, userId);
    }

    /* 전체 검색 (페이지화 X) */
//    @GetMapping("/search")
//    public List<Post> searchCommPost(@RequestParam String searchType, @RequestParam String searchWord) {
//        return comService.searchCommPost(searchType, searchWord);
//    }

    /* 전체 검색 (페이지화) */
    @GetMapping("/search")
    public Page<Post> searchCommPost(@RequestParam String searchType, @RequestParam String searchWord,
                                     @RequestParam(value = "page", defaultValue = "1", required = false)  int page) {
        return comService.searchCommPost(searchType, searchWord, page);
    }

    /* 카테고리 - 전체 검색 (페이지화 X) */
//    @GetMapping("/search/category")
//    public List<Post> searchCommPostByCategory(@RequestParam String searchType, @RequestParam String category,
//                                               @RequestParam String searchWord) {
//        return comService.searchCommPostByCategory(searchType, category, searchWord);
//    }

    /* 카테고리 - 전체 검색 (페이지화) */
    @GetMapping("/search/category")
    public Page<Post> searchCommPostByCategory(@RequestParam String searchType, @RequestParam String category,
                                               @RequestParam String searchWord, @RequestParam(value = "page", defaultValue = "1", required = false)  int page) {
        return comService.searchCommPostByCategory(searchType, category, searchWord, page);
    }

    /* 커뮤니티 게시글 등록 */
    @PostMapping
    public Post registerCommPost(@RequestBody Post requestPost, Authentication authentication) {
        Post post = comService.registerCommPost(requestPost, authentication);
        return post;
    }

    /* 커뮤니티 게시글 수정 */
    @PostMapping("/{id}")
    public Post updateCommPost(@PathVariable Long id, @RequestBody CommPostRequestDto requestPost, Authentication authentication) {
        Post post = comService.updateCommPost(id, requestPost, authentication);
        return post;
    }

    /* 커뮤니티 게시글 삭제 */
    @DeleteMapping("/{id}")
    public boolean deleteCommPost(@PathVariable Long id, Authentication authentication) {
        return comService.deleteCommPost(id, authentication);
    }
}
