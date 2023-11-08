package com.web.stard.repository;

import com.web.stard.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AssigneeRepository extends JpaRepository<Assignee, AssigneeId> {

    /* 해당 TO DO의 담당자 조회 */
    List<Assignee> findByToDo(ToDo toDo);

    /* 해당 TO DO 담당자에 존재하는지 */
    boolean existsByToDoAndMember(ToDo toDo, Member member);
    /* 해당 TO DO 담당자 */
    Optional<Assignee> findByToDoAndMember(ToDo toDo, Member member);

    /* 사용자의 모든 TO DO */
    List<Assignee> findAllByMember(Member member);
    /* 사용자의 모든 TO DO (월 단위!) */
    List<Assignee> findAllByMemberAndToDoDueDateBetween(Member member, LocalDateTime start, LocalDateTime end);

    /* 사용자의 스터디 내 모든 TO DO */
    List<Assignee> findAllByMemberAndToDoStudy(Member member, Study study);
    /* 사용자의 스터디 내 모든 TO DO (월 단위!) */
    List<Assignee> findAllByMemberAndToDoStudyAndToDoDueDateBetween(Member member, Study study, LocalDateTime start, LocalDateTime end);
}
