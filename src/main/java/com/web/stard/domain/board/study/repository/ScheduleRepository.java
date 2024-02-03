package com.web.stard.domain.board.study.repository;

import com.web.stard.domain.board.study.domain.Schedule;
import com.web.stard.domain.board.study.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    /* 스터디 내 모든 일정 */
    List<Schedule> findAllByStudy(Study study);
    /* 스터디 내 모든 일정 (월 단위로!) */
    List<Schedule> findAllByStudyAndStartDateBetween(Study study, LocalDateTime start, LocalDateTime end);

}
