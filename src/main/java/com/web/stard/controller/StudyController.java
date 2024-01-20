package com.web.stard.controller;

import com.web.stard.domain.Applicant;
import com.web.stard.domain.RecruitStatus;
import com.web.stard.domain.Study;
import com.web.stard.dto.StudyDto;
import com.web.stard.dto.response.Top5Dto;
import com.web.stard.service.StudyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/studies")
public class StudyController {

    private final StudyService studyService;


    @GetMapping("/all")     // [R] 스터디 게시글 전체 조회 ( 모집 중 / 모집 완료 순으로)
    public Page<Study> getAllStudies(@RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return studyService.findAllByOrderByRecruitStatus(page);
    }


    @GetMapping("/{id}")    // [R] 스터디 게시글 세부 조회
    public Study getStudy(Authentication authentication, @PathVariable Long id){
        return studyService.getStudyDetail(authentication, id);
    }

    // [R] 키워드로 스터디 게시글 검색 ( 모집 중, 모집 완료 순으로 )

    @GetMapping("/search-by-title")     // [R] 키워드(제목)로 스터디 게시글 검색
    public Page<Study> getStudiesByTitle(@RequestParam("keyword") String keyword, @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        System.out.println("진입 1 : " + keyword);
        return studyService.findByTitleContainingOrderByRecruitStatus(keyword, page);
    }

    @GetMapping("/search-by-content")     // [R] 키워드(내용)로 스터디 게시글 검색
    public Page<Study> getStudiesByContent(@RequestParam("keyword") String keyword, @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return studyService.findByContentContainingOrderByRecruitStatus(keyword, page);
    }

    @GetMapping("/search-by-recruiter")     // [R] 키워드(작성자)로 스터디 게시글 검색
    public Page<Study> getStudiesByRecruiter(@RequestParam("keyword") String keyword, @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        return studyService.findByRecruiter_NicknameContainingOrderByRecruitStatus(keyword, page);
    }

    // [R] 모집 중인 게시글 중에서 키워드 검색
    @GetMapping("/search-by-title-in-recruiting-studies")     // [R] 키워드(제목)로 스터디 게시글 검색
    public Page<Study> getRecruitingStudiesByTitle(@RequestParam("keyword") String keyword, @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        RecruitStatus recruitStatus = RecruitStatus.valueOf("RECRUITING");
        return studyService.findByTitleContainingAndRecruitStatus(keyword, recruitStatus, page);
    }

    @GetMapping("/search-by-content-in-recruiting-studies")     // [R] 키워드(내용)로 스터디 게시글 검색
    public Page<Study> getRecruitingStudiesByContent(@RequestParam("keyword") String keyword, @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        RecruitStatus recruitStatus = RecruitStatus.valueOf("RECRUITING");
        return studyService.findByContentContainingAndRecruitStatus(keyword, recruitStatus, page);
    }

    @GetMapping("/search-by-recruiter-in-recruiting-studies")     // [R] 키워드(작성자)로 스터디 게시글 검색
    public Page<Study> getRecruitingStudiesByRecruiter(@RequestParam("keyword") String keyword, @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        RecruitStatus recruitStatus = RecruitStatus.valueOf("RECRUITING");
        return studyService.findByRecruiterContainingAndRecruitStatus(keyword, recruitStatus, page);
    }


    // [R] 모집 완료된 게시글 중에서 키워드 검색

    @GetMapping("/search-by-title-in-recruited-studies")     // [R] 키워드(제목)로 스터디 게시글 검색
    public Page<Study> getRecruitedStudiesByTitle(@RequestParam("keyword") String keyword, @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        RecruitStatus recruitStatus = RecruitStatus.valueOf("RECRUITMENT_COMPLETE");
        return studyService.findByTitleContainingAndRecruitStatus(keyword, recruitStatus, page);
    }

    @GetMapping("/search-by-content-in-recruited-studies")     // [R] 키워드(내용)로 스터디 게시글 검색
    public Page<Study> getRecruitedStudiesByContent(@RequestParam("keyword") String keyword, @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        RecruitStatus recruitStatus = RecruitStatus.valueOf("RECRUITMENT_COMPLETE");
        return studyService.findByContentContainingAndRecruitStatus(keyword, recruitStatus, page);
    }

    @GetMapping("/search-by-recruiter-in-recruited-studies")     // [R] 키워드(작성자)로 스터디 게시글 검색
    public Page<Study> getRecruitedStudiesByRecruiter(@RequestParam("keyword") String keyword, @RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        RecruitStatus recruitStatus = RecruitStatus.valueOf("RECRUITMENT_COMPLETE");
        return studyService.findByRecruiterContainingAndRecruitStatus(keyword, recruitStatus, page);
    }

    // [R] 모집 중인 게시글 조회
    @GetMapping("/search-recruiting-studies")
    public Page<Study> getRecruitingStudies(@RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        RecruitStatus recruitStatus = RecruitStatus.valueOf("RECRUITING");
        return studyService.findByRecruitStatus(recruitStatus, page);
    }

    // [R] 모집 완료된 게시글 조회
    @GetMapping("/search-recruited-studies")
    public Page<Study> getRecruitedStudies(@RequestParam(value = "page", defaultValue = "1", required = false) int page) {
        RecruitStatus recruitStatus = RecruitStatus.valueOf("RECRUITMENT_COMPLETE");
        return studyService.findByRecruitStatus(recruitStatus, page);
    }

    @PostMapping       // [C] 스터디 게시글 생성
    public Study createStudy(@RequestBody StudyDto studyDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return studyService.createStudy(studyDto, authentication);
    }


    @DeleteMapping("/{id}")      // [D] 스터디 게시글 삭제
    public boolean deleteStudy(@PathVariable long id, Authentication authentication){
        return studyService.deleteStudy(id, authentication);
    }

    @PutMapping("/{id}")     // [U] 스터디 게시글 수정
    public Study updateStudy(@PathVariable long id, @RequestBody StudyDto studyDto, Authentication authentication){
        System.out.println("진입" +  studyDto);
        return studyService.updateStudy(id, studyDto, authentication);
    }

    @PostMapping("/{id}/apply")       // [C] 스터디 신청
    public Study createApplicant(@PathVariable long id, @RequestParam String apply_reason, Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        return studyService.createApplicant(id, apply_reason, authentication);
    }

    @GetMapping("/{id}/apply")       // [C] 로그인한 사용자의 스터디 신청 확인 여부
    public boolean isApplicant(@PathVariable long id, Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        return studyService.isApplicant(id, authentication);
    }

    @GetMapping("/{id}/apply-reason")       // [C] 로그인한 사용자의 스터디 지원 신청 확인 여부 및 applicant return
    public Applicant findApplicant(@PathVariable long id, Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        return studyService.findByMemberAndStudy(id, authentication);
    }

    @PutMapping("/{id}/select")       // [U] 스터디 신청자 선택 ( 수락 / 거절 )
    public ResponseEntity<String> selectParticipant(@PathVariable long id, @RequestParam String applicantId, @RequestParam boolean isSelect, Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        try {
            studyService.selectParticipant(id, applicantId, isSelect, authentication);
            return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("NOT SUCCESS", HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/{id}/select")       // [R] 스터디 신청자 리스트 Select
    public ResponseEntity<Map<String, Object>> findParticipants(@PathVariable long id, Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new HashMap<>();
        try{
            result.put("data", studyService.getParticipants(id, authentication));
            return ResponseEntity.ok().body(result);
        }catch (Exception e) {
            result.put("data", "스터디 개설자가 아닙니다.");
            return ResponseEntity.badRequest().body(result);
        }
    }


    @GetMapping("/{id}/recruiter")       // [R] 로그인한 사용자의 스터디 개설자 확인 여부
    public boolean isRecruiter(@PathVariable long id, Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        return studyService.isRecruiter(id, authentication);
    }

    @PostMapping("/{id}/open")       // [C] 스터디 모집 완료
    public ResponseEntity<String> openStudy(@PathVariable long id, Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            studyService.openStudy(id, authentication);
            return new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>("NOT SUCCESS", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}/study-member")       // [R] 스터디 참여자 리스트 Select
    public ResponseEntity<Map<String, Object>> findStudyMember(@PathVariable long id, Authentication authentication) {
        authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new HashMap<>();
        try{
            result.put("data", studyService.findStudyMember(id, authentication));
            return ResponseEntity.ok().body(result);
        }catch (Exception e) {
            result.put("data", "스터디 참여자 리스트 가져오기 실패");
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/study-ranking")       // [R] 스터디 참여자 리스트 Select
    public ResponseEntity<Map<String, Object>> findStudyRanking() {
        Map<String, Object> result = new HashMap<>();
        try{
            result.put("data", studyService.findStudyRanking());
            return ResponseEntity.ok().body(result);
        }catch (Exception e) {
            System.out.println(e);
            result.put("data", "스터디 분야 Top 5 가져오기 실패");
            return ResponseEntity.badRequest().body(result);
        }
    }

    @GetMapping("/discontinue/{studyId}") // 스터디 중단 동의 여부 (사용자 본인만)
    public Boolean findStudyDiscontinueAllow(@PathVariable Long studyId, Authentication authentication) {
        return studyService.findStudyDiscontinueAllow(studyId, authentication);
    }

    @PostMapping("/discontinue/{studyId}")  // 스터디 중단 동의
    public Boolean studyDiscontinueAllow(@PathVariable Long studyId, Authentication authentication) throws Exception {
        return studyService.studyDiscontinueAllow(studyId, authentication);
    }


}
