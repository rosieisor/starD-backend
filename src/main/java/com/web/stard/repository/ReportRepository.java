package com.web.stard.repository;

import com.web.stard.domain.Member;
import com.web.stard.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Report findByPostId(Long postId);

    Report findByStudyId(Long studyId);

    Report findByReplyId(Long replyId);

    Report findByStudyPostId(Long studyPostId);

    List<Report> findByMember(Member member);
}
