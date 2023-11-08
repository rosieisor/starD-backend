package com.web.stard.repository;

import com.web.stard.domain.Member;
import com.web.stard.domain.RecruitStatus;
import com.web.stard.domain.Study;
import com.web.stard.domain.StudyType;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudyRepository extends JpaRepository<Study, Long> {

    Page<Study> findAll(Pageable pageable);

    Page<Study> findAllByOrderByRecruitStatus(Pageable pageable);

    Page<Study> findByRecruiter(Member member , Pageable pageable);

    Page<Study> findByTitleContainingOrderByRecruitStatus(String keyword, Pageable pageable);

    Page<Study> findByContentContainingOrderByRecruitStatus(String keyword, Pageable pageable);

    Page<Study> findByRecruiterContainingOrderByRecruitStatus(String keyword, Pageable pageable);

    Page<Study> findByRecruitStatus(RecruitStatus recruitStatus, Pageable pageable);

    Page<Study> findByTitleContainingAndRecruitStatus(String keyword, RecruitStatus recruitStatus, Pageable pageable);

    Page<Study> findByContentContainingAndRecruitStatus(String keyword, RecruitStatus recruitStatus, Pageable pageable);

    Page<Study> findByRecruiterContainingAndRecruitStatus(String keyword, RecruitStatus recruitStatus, Pageable pageable);

}
