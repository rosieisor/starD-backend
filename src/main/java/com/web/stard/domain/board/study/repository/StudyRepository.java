package com.web.stard.domain.board.study.repository;

import com.web.stard.domain.board.study.domain.enums.RecruitStatus;
import com.web.stard.domain.board.study.domain.Study;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.board.study.domain.enums.ProgressStatus;
import com.web.stard.domain.board.study.dto.Top5Dto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface StudyRepository extends JpaRepository<Study, Long> {

    Page<Study> findAll(Pageable pageable);

    Page<Study> findAllByOrderByRecruitStatus(Pageable pageable);

    Page<Study> findByRecruiter(Member member , Pageable pageable);

    Page<Study> findByTitleContainingOrderByRecruitStatus(String keyword, Pageable pageable);

    Page<Study> findByContentContainingOrderByRecruitStatus(String keyword, Pageable pageable);

    Page<Study> findByRecruiter_NicknameContainingOrderByRecruitStatus(String keyword, Pageable pageable);

    Page<Study> findByRecruitStatus(RecruitStatus recruitStatus, Pageable pageable);

    Page<Study> findByTitleContainingAndRecruitStatus(String keyword, RecruitStatus recruitStatus, Pageable pageable);

    Page<Study> findByContentContainingAndRecruitStatus(String keyword, RecruitStatus recruitStatus, Pageable pageable);

    Page<Study> findByRecruiterContainingAndRecruitStatus(String keyword, RecruitStatus recruitStatus, Pageable pageable);

    @Query("SELECT new com.web.stard.dto.response.Top5Dto(s.field, COUNT(s.field)) FROM Study AS s GROUP BY s.field ORDER BY COUNT(s.field) DESC")
    List<Top5Dto> findTop5();

    List<Study> findByRecruitmentDeadlineBefore(LocalDate localDate);

    List<Study> findByActivityDeadlineBeforeAndProgressStatus(LocalDate localDate, ProgressStatus progressStatus);

    List<Study> findByActivityStartGreaterThanEqualAndProgressStatus(LocalDate localDate, ProgressStatus progressStatus);


    /* 개설자로 progressStatus가 null인 스터디 검색 */
    @Query("SELECT s FROM Study s WHERE s.recruiter = :recruiter AND s.progressStatus IS NULL")
    List<Study> findStudiesByRecruiterAndNullProgressStatus(Member recruiter);
    /* 진행 완료된 스터디 (개설자로 검색) */
    List<Study> findByRecruiterAndProgressStatus(Member member, ProgressStatus progressStatus);
}
