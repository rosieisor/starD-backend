package com.web.stard.domain.board.global.repository;

import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.board.global.domain.Report;
import com.web.stard.domain.board.global.domain.ReportDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportDetailRepository extends JpaRepository<ReportDetail, Long> {
    ReportDetail findByReportAndMember(Report report, Member currentUser);

    long countByReportId(Long reportId);

    List<ReportDetail> findByReportId(Long reportId);
}
