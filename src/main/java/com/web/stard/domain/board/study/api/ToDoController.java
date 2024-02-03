package com.web.stard.domain.board.study.api;

import com.web.stard.domain.board.study.domain.Assignee;
import com.web.stard.domain.board.study.domain.ToDo;
import com.web.stard.domain.board.study.dto.ToDoDto;
import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.board.study.application.StudyService;
import com.web.stard.domain.board.study.application.TodoService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@RestController
@RequestMapping("/todo")
public class ToDoController {

    private final MemberService memberService;
    private final TodoService todoService;
    private final StudyService studyService;

    /* 사용자 TO DO 조회 (전체) */
    @GetMapping("/all")
    public List<Assignee> getAllToDoListByMember(@RequestParam int year, @RequestParam int month,
                                                 Authentication authentication) {
        return todoService.getAllToDoListByMember(year, month, authentication);
    }

    /* 사용자 TO DO 조회 (스터디별) */
    @GetMapping("/user/{studyId}")
    public List<Assignee> getToDoListByMemberAndStudy(@PathVariable Long studyId, @RequestParam int year,
                                                      @RequestParam int month, Authentication authentication) {
        return todoService.getToDoListByMemberAndStudy(studyId, year, month, authentication);
    }

    /* 스터디 내 모든 TO DO 조회 */
    @GetMapping("/{studyId}")
    public List<ToDoDto> getAllToDoListByStudy(@PathVariable Long studyId, @RequestParam int year, @RequestParam int month) {
        return todoService.getAllToDoListByStudy(studyId, year, month);
    }


    /* TO DO 등록 */
    @PostMapping
    public ToDoDto registerTodo(@RequestParam Long studyId, @RequestBody ToDo toDo,
                             @RequestParam String assigneeStr, Authentication authentication) {
        // TODO : 권한 확인 (스터디원인지) -> 동작 확인 필요
        if (!studyService.checkStudyMember(studyId, authentication.getName())) {
            return null;
        }

        return todoService.registerTodo(studyId, toDo, assigneeStr);
    }

    /* TO DO 수정 */
    @PutMapping ("/{toDoId}")
    public ToDoDto updateTodo(@PathVariable Long toDoId, @RequestBody ToDo updateToDo,
                           @RequestParam String assigneeStr, Authentication authentication) {
        // TODO : 권한 확인 (스터디원인지) -> 동작 확인 필요
        if (!todoService.checkStudyMemberByToDo(toDoId, authentication.getName())) {
            return null;
        }

        return todoService.updateTodo(toDoId, updateToDo, assigneeStr);
    }

    /* TO DO 삭제 */
    @DeleteMapping("/{toDoId}")
    public boolean deleteTodo(@PathVariable Long toDoId, Authentication authentication) {
        // TODO : 권한 확인 (스터디원인지) -> 동작 확인 필요
        if (!todoService.checkStudyMemberByToDo(toDoId, authentication.getName())) {
            return false;
        }

        return todoService.deleteTodo(toDoId);
    }

    /* TO DO 상태 변화 (완료, 미완료) */
    @PostMapping("/{toDoId}/status")
    public Assignee updateTodoStatus(@PathVariable Long toDoId, boolean status, Authentication authentication) {
        return todoService.updateTodoStatus(toDoId, authentication, status);
    }
}
