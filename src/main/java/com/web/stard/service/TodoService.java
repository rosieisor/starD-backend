package com.web.stard.service;

import com.web.stard.domain.Assignee;
import com.web.stard.domain.Member;
import com.web.stard.domain.Study;
import com.web.stard.domain.ToDo;
import com.web.stard.dto.ToDoDto;
import com.web.stard.repository.AssigneeRepository;
import com.web.stard.repository.TodoRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter @Setter
@AllArgsConstructor
@Service
public class TodoService {

    StudyService studyService;
    MemberService memberService;

    TodoRepository todoRepository;
    AssigneeRepository assigneeRepository;

    /* TO DO 조회 (하나) */
    public ToDo getToDo(Long id) {
        Optional<ToDo> result = todoRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /* 담당자 조회 (리스트) */
    public List<Assignee> getAssignee(Long id) {
        ToDo toDo = getToDo(id);
        return assigneeRepository.findByToDo(toDo);
    }

    /* 담당자 조회 (개별) */
    public Assignee getAssigneeByToDoAndMember(ToDo toDo, Member member) {
        Optional<Assignee> result = assigneeRepository.findByToDoAndMember(toDo, member);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /* 사용자 TO DO 조회 (전체) */
    public List<Assignee> getAllToDoListByMember(int year, int month, Authentication authentication) {
        Member member = memberService.find(authentication.getName());

        // 월 단위로 검색
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month, YearMonth.of(year, month).lengthOfMonth(),
                23, 59, 59);

        return assigneeRepository.findAllByMemberAndToDoDueDateBetween(member, start, end);
    }

    /* 사용자 TO DO 조회 (스터디별) */
    public List<Assignee> getToDoListByMemberAndStudy(Long studyId, int year, int month, Authentication authentication) {
        Member member = memberService.find(authentication.getName());
        Study study = studyService.findById(studyId);

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month, YearMonth.of(year, month).lengthOfMonth(),
                23, 59, 59);

        return assigneeRepository.findAllByMemberAndToDoStudyAndToDoDueDateBetween(member, study, start, end);
    }

    /* 스터디 내 모든 TO DO 조회 */
    public List<ToDoDto> getAllToDoListByStudy(Long studyId, int year, int month) {
        Study study = studyService.findById(studyId);

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month, YearMonth.of(year, month).lengthOfMonth(),
                23, 59, 59);

        List<ToDo> toDoList = todoRepository.findAllByStudyAndDueDateBetween(study, start, end);
        List<ToDoDto> toDoDtoList = null;

        for (ToDo t : toDoList) {
            if (toDoDtoList == null) {
                toDoDtoList = new ArrayList<>();
            }

            ToDoDto toDoDto = new ToDoDto(
                    t.getId(), t.getStudy(), t.getTask(), t.getDueDate()
            );
            List<Assignee> assignees = getAssignee(t.getId());
            toDoDto.setAssignees(assignees);

            toDoDtoList.add(toDoDto);
        }

        return toDoDtoList;
    }




    /* TO DO 등록 */
    @Transactional
    public ToDoDto registerTodo(Long studyId, ToDo toDo, String assigneeStr) {
        Study study = studyService.findById(studyId);
        toDo.setStudy(study);

        todoRepository.save(toDo); // TO DO 등록

        // 담당자 배정
        String[] assigneeMem = assigneeStr.split(",");
        List<Assignee> assignees = new ArrayList<>();
        for (String ass : assigneeMem) {
            Member member = memberService.findByNickname(ass);

            Assignee assignee = new Assignee();
            assignee.setToDo(toDo);
            assignee.setMember(member);
            assignee.setToDoStatus(false);

            assignees.add(assignee);
        }

        assigneeRepository.saveAll(assignees);

        ToDoDto toDoDto = new ToDoDto(
                toDo.getId(), toDo.getStudy(), toDo.getTask(), toDo.getDueDate()
        );

        toDoDto.setAssignees(assignees);

        return toDoDto;
    }

    /* TO DO 수정 */
    @Transactional
    public ToDoDto updateTodo(Long toDoId, ToDo updateToDo, String assigneeStr) {
        ToDo toDo = getToDo(toDoId);
        List<Assignee> initialAssignee = getAssignee(toDoId); // 기존 담당자

        toDo.setTask(updateToDo.getTask());
        toDo.setDueDate(updateToDo.getDueDate());

        todoRepository.save(toDo); // TO DO 수정

        // 기존 담당자, 새로운 담당자 비교
        String[] assigneeMem = assigneeStr.split(",");
        for (String ass : assigneeMem) {
            Member member = memberService.find(ass);

            if (assigneeRepository.existsByToDoAndMember(toDo, member)) { // 기존 담당자인 경우
                // 기존 담당자 리스트에서 제거
                Optional<Assignee> result = assigneeRepository.findByToDoAndMember(toDo, member);
                Assignee assignee = result.get();
                initialAssignee.remove(assignee);
            } else { // 기존 담당자가 아닌 경우
                // 새로 추가
                Assignee assignee = new Assignee();
                assignee.setToDo(toDo);
                assignee.setMember(member);
                assignee.setToDoStatus(false);

                assigneeRepository.save(assignee);
            }
        }

        // 리스트에 남아 있는 담당자들은 삭제 (수정된 담당자에서 제외된 경우)
        assigneeRepository.deleteAll(initialAssignee);

        ToDoDto toDoDto = new ToDoDto(
                toDo.getId(), toDo.getStudy(), toDo.getTask(), toDo.getDueDate()
        );

        List<Assignee> assignees = getAssignee(toDo.getId());
        toDoDto.setAssignees(assignees);

        return toDoDto;
    }

    /* TO DO 삭제 */
    @Transactional
    public boolean deleteTodo(Long toDoId) {
        ToDo toDo = getToDo(toDoId);
        List<Assignee> assignees = getAssignee(toDoId);

        assigneeRepository.deleteAll(assignees); // 담당자 삭제
        todoRepository.delete(toDo); // TO DO 삭제

        if (getToDo(toDoId) == null) { // 삭제됨
            return true;
        } return false;
    }

    /* TO DO 상태 변화 (완료, 미완료) */
    public Assignee updateTodoStatus(Long toDoId, Authentication authentication, boolean status) {
        ToDo toDo = getToDo(toDoId);
        Member member = memberService.find(authentication.getName());

        Assignee assignee = getAssigneeByToDoAndMember(toDo, member);
        assignee.setToDoStatus(status);

        assigneeRepository.save(assignee);

        return assignee;
    }

    /* 스터디원인지 확인 */
    public boolean checkStudyMemberByToDo(Long toDoId, String id) {
        ToDo toDo = getToDo(toDoId);
        return studyService.checkStudyMember(toDo.getStudy().getId(), id);
    }
}
