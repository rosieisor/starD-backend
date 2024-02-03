package com.web.stard.domain.board.study.api;

import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.board.study.domain.Schedule;
import com.web.stard.domain.board.study.application.ScheduleService;
import com.web.stard.domain.board.study.application.StudyService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    private final MemberService memberService;
    private final ScheduleService scheduleService;
    private final StudyService studyService;

    /* 사용자 일정 조회 (전체) */
    @GetMapping("/all")
    public List<Schedule> getAllToDoListByMember(@RequestParam int year, @RequestParam int month, Authentication authentication) {
        return scheduleService.getAllScheduleListByMember(year, month, authentication);
    }

    /* 사용자 일정 조회 (스터디별) */
    @GetMapping("/{studyId}")
    public List<Schedule> getToDoListByMemberAndStudy(@PathVariable Long studyId, @RequestParam int year, @RequestParam int month) {
        return scheduleService.getScheduleListByStudy(studyId, year, month);
    }

    /* 일정 등록 */
    @PostMapping
    public Schedule registerSchedule(@RequestParam Long studyId, @RequestBody Schedule schedule,
                                     Authentication authentication) {
        // TODO : 권한 확인 (스터디원인지) -> 동작 확인 필요
        if (!studyService.checkStudyMember(studyId, authentication.getName())) {
            return null;
        }

        return scheduleService.registerSchedule(studyId, schedule);
    }

    /* 일정 수정 */
    @PutMapping("/{scheduleId}")
    public Schedule updateSchedule(@PathVariable Long scheduleId, @RequestParam String title,
                                   @RequestParam String color, Authentication authentication) {
        // TODO : 권한 확인 (스터디원인지) -> 동작 확인 필요
        if (!scheduleService.checkStudyMemberBySchedule(scheduleId, authentication.getName())) {
            return null;
        }

        return scheduleService.updateSchedule(scheduleId, title, color);
    }

    /* 일정 삭제 */
    @DeleteMapping("/{scheduleId}")
    public boolean deleteSchedule(@PathVariable Long scheduleId, Authentication authentication) {
        // TODO : 권한 확인 (스터디원인지) -> 동작 확인 필요
        if (!scheduleService.checkStudyMemberBySchedule(scheduleId, authentication.getName())) {
            return false;
        }

        return scheduleService.deleteSchedule(scheduleId);
    }
}
