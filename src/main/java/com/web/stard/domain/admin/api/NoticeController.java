package com.web.stard.domain.admin.api;

import com.web.stard.domain.board.global.domain.Post;
import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.admin.application.NoticeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Transactional
@Getter
@Setter
@RequestMapping("/notice")
@AllArgsConstructor
@RestController
public class NoticeController {

    private final MemberService memberService;
    private  final NoticeService noticeService;

    // Notice 등록
    @PostMapping
    public Post createNotice(@RequestBody Post post, Authentication authentication) {
        noticeService.createNotice(post, authentication);
        return post;
    }

    // Notice 리스트 조회 - 페이지화x
//    @GetMapping
//    public List<Post> getAllNotice() {
//        return noticeService.getAllNotice();
//    }

    // Notice 리스트 조회 - 페이지화o
    @GetMapping
    public Page<Post> getAllNotice(@RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return noticeService.getAllNotice(page);
    }

    // Notice 상세 조회
    @GetMapping("/{id}")
    public Post getNoticeDetail(@PathVariable Long id) {
        String userId = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            if (!authentication.getName().equals("anonymousUser")) {
                userId = authentication.getName(); // 사용자 아이디
            }
        }
        return noticeService.getNoticeDetail(id, userId);
    }

    // Notice 수정
    @PostMapping("/{id}")
    public Post updateNotice(@PathVariable Long id, @RequestBody Post requestPost, Authentication authentication) {
        Post post = noticeService.updateNotice(id, requestPost, authentication);
        return post;
    }

    // Notice 삭제
    @DeleteMapping("/{id}")
    public void deleteNotice(@PathVariable Long id, Authentication authentication) {
        noticeService.deleteNotice(id, authentication);
    }

    // id로 타입 조회
    @GetMapping("/find-type/{id}")
    public Optional<Post> findTypeById(@PathVariable Long id, Authentication authentication) {
        return noticeService.findTypeById(id, authentication);
    }

    // 전체 검색(페이지화x)
//    @GetMapping("/search")
//    public List<Post> searchCommPost(@RequestParam String searchType, @RequestParam String searchWord) {
//        return noticeService.searchNoticePost(searchType, searchWord);
//    }

    // 전체 검색(페이지화o)
    @GetMapping("/search")
    public Page<Post> searchCommPost(@RequestParam String searchType, @RequestParam String searchWord,
                                     @RequestParam(value = "page", defaultValue = "1", required = false)  int page) {
        return noticeService.searchNoticePost(searchType, searchWord, page);
    }
}
