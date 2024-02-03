package com.web.stard.domain.board.study.repository;

import com.web.stard.domain.board.study.domain.Applicant;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.board.study.domain.Study;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

    boolean existsByMemberAndStudy(Member member, Study study);

    Applicant findByMemberAndStudy(Member member, Study study);

    Page<Applicant> findByMember(Member member, Pageable page);

    List<Applicant> findByStudy(Study study);

    List<Applicant> findByMemberAndStudyAndParticipationState(Member member, Study study, boolean participationState);

    List<Applicant> findByStudyAndParticipationState(Study study, boolean participationState);


    List<Applicant> findByMemberAndStudyProgressStatusIsNotNull(Member member);
}
