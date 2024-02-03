package com.web.stard.domain.board.global.repository;

import com.web.stard.domain.board.global.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findByPostId(Long postId);

    Report findByStudyId(Long studyId);

    Report findByReplyId(Long replyId);

    Report findByStudyPostId(Long studyPostId);

}
