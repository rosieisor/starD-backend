package com.web.stard.domain.board.global.repository;

import com.web.stard.domain.board.global.domain.Report;
import com.web.stard.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findByPostId(Long postId);

    Report findByStudyId(Long studyId);

    Report findByReplyId(Long replyId);

    Report findByStudyPostId(Long studyPostId);

    List<Report> findByMember(Member member);
}
