package com.web.stard.domain.board.study.repository;

import com.web.stard.domain.board.study.domain.Study;
import com.web.stard.domain.board.study.domain.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepository extends JpaRepository<ToDo, Long> {

    /* 스터디 내 모든 TO DO */
    List<ToDo> findAllByStudy(Study study);
    /* 스터디 내 모든 TO DO (월 검색) */
    List<ToDo> findAllByStudyAndDueDateBetween(Study study, LocalDateTime start, LocalDateTime end);

}
