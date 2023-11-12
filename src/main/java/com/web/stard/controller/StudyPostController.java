package com.web.stard.controller;

import com.web.stard.domain.StudyPost;
import com.web.stard.service.StudyPostService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@RestController
@RequestMapping("/study/post")
public class StudyPostController {

    private final StudyPostService studyPostService;


    /* 게시글 전체 조회 */
    @GetMapping
    public List<StudyPost> getStudyPostList(@RequestParam Long studyId) {
        return studyPostService.getStudyPostList(studyId);
    }

    /* 게시글 상세 조회 */
    @GetMapping("/{postId}")
    public StudyPost getStudyPost(@PathVariable Long postId) {
        return studyPostService.getStudyPost(postId);
    }

    /* 게시글 등록 */
    @PostMapping
    public StudyPost registerPost(@RequestParam Long studyId, @RequestParam String title,
                                  @RequestParam String content, @RequestParam MultipartFile file,
                                  Authentication authentication) {
        return studyPostService.registerPost(studyId, title, content, file, authentication);
    }

    /* 게시글 수정 */
    @PostMapping("/{postId}")
    public StudyPost updatePost(@PathVariable Long postId, @RequestParam String title,
                                @RequestParam String content, @RequestParam MultipartFile file,
                                Authentication authentication) {
        return studyPostService.updatePost(postId, title, content, file);
    }

    /* 게시글 삭제 */
    @DeleteMapping("{postId}")
    public boolean deletePost(@PathVariable Long postId, Authentication authentication) {
        return studyPostService.deletePost(postId);
    }
}
