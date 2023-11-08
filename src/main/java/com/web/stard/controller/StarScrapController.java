package com.web.stard.controller;

import com.web.stard.domain.StarScrap;
import com.web.stard.domain.Study;
import com.web.stard.service.StarScrapService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@RestController
public class StarScrapController {

    private final StarScrapService starScrapService;



    /* Post(community) 공감 추가 */
    @PostMapping("/star/post/{id}")
    public StarScrap addPostStar(@PathVariable Long id, Authentication authentication) {
        return starScrapService.addPostStar(id, authentication);
    }

    /* Post(Community) 공감 삭제 */
    @DeleteMapping("/star/post/{id}")
    public boolean deletePostStar(@PathVariable Long id, Authentication authentication) {
        return starScrapService.deletePostStar(id, authentication);
    }


    /* 특정 스터디 공감 여부 */
    @GetMapping("/star/study/{id}")
    public Boolean getStudyStar(@PathVariable Long id, Authentication authentication) {
        return starScrapService.getStudyStar(id, authentication);
    }

    /* ScrapStudySlide 공감 추가 */
    @PostMapping("/star/study/{id}")
    public StarScrap addStudyStar(@PathVariable Long id, Authentication authentication) {
        return starScrapService.addStudyStar(id, authentication);
    }

    /* ScrapStudySlide 공감 삭제 */
    @DeleteMapping("/star/study/{id}")
    public boolean deleteStudyStar(@PathVariable Long id, Authentication authentication) {
        return starScrapService.deleteStudyStar(id, authentication);
    }




    /* Post(community) 스크랩한 게시글 전체 조회 */
    @GetMapping("/scrap/post")
    public List<Study> allPostScrapList(Authentication authentication) {
        return starScrapService.allPostScrapList(authentication);
    }

    /* Post(community) 스크랩 추가 */
    @PostMapping("/scrap/post/{id}")
    public StarScrap addPostScrap(@PathVariable Long id, Authentication authentication) {
        return starScrapService.addPostScrap(id, authentication);
    }

    /* Post(Community) 스크랩 삭제 */
    @DeleteMapping("/scrap/post/{id}")
    public boolean deletePostScrap(@PathVariable Long id, Authentication authentication) {
        return starScrapService.deletePostScrap(id, authentication);
    }



    /* ScrapStudySlide 스크랩한 게시글 전체 조회 */
    @GetMapping("/scrap/study")
    public List<Study> allStudyScrapList(Authentication authentication) {
        return starScrapService.allStudyScrapList(authentication);
    }

    /* 특정 스터디 스크랩 여부 */
    @GetMapping("/scrap/study/{id}")
    public Boolean getStudyScrap(@PathVariable Long id, Authentication authentication) {
        return starScrapService.getStudyScrap(id, authentication);
    }

    /* ScrapStudySlide 스크랩 추가 */
    @PostMapping("/scrap/study/{id}")
    public StarScrap addStudyScrap(@PathVariable Long id, Authentication authentication) {
        return starScrapService.addStudyScrap(id, authentication);
    }

    /* ScrapStudySlide 스크랩 삭제 */
    @DeleteMapping("/scrap/study/{id}")
    public boolean deleteStudyScrap(@PathVariable Long id, Authentication authentication) {
        return starScrapService.deleteStudyScrap(id, authentication);
    }



    /* 스터디 페이지의 공감 여부 조회 */
    @GetMapping("/study/stars")
    public List<Boolean> getStudyPageStar(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                          Authentication authentication) {
        return starScrapService.getStudyPageStar(page, authentication);
    }

    /* 스터디 페이지의 스크랩 여부 조회 */
    @GetMapping("/study/scraps")
    public List<Boolean> getStudyPageScrap(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                           Authentication authentication) {
        return starScrapService.getStudyPageScrap(page, authentication);
    }

    /* 스크랩한 스터디의 공감 여부 조회 */
    @GetMapping("/study/stars/scraps")
    public List<Boolean> getStudyPageStarByScrap(Authentication authentication) {
        return starScrapService.getStudyPageStarByScrap(authentication);
    }

    /* 마이페이지 - 스터디 공감, 스크랩 조회 */
    @GetMapping("/mypage/study/star-scrap")
    public List<Boolean> getMyPageStudyStarScraps(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                  Authentication authentication,
                                                  @RequestParam String status, @RequestParam String type) {
        return starScrapService.getMyPageStudyStarScrap(page, authentication, status, type);
    }
}
