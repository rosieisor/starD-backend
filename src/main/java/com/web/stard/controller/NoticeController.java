package com.web.stard.controller;

import com.web.stard.domain.Post;
import com.web.stard.service.MemberService;
import com.web.stard.service.NoticeService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

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

    // Notice 리스트 조회
    @GetMapping
    public List<Post> getAllNotice() {
        return noticeService.getAllNotice();
    }

    // 페이지화
/*
    public List<Post> getAllNotice(@RequestParam("page") int page) {
        return noticeService.getAllNotice(page);
    }
*/


    // Notice 상세 조회
    @GetMapping("/{id}")
    public Post getNoticeDetail(@PathVariable Long id) {
        return noticeService.getNoticeDetail(id);
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
}
