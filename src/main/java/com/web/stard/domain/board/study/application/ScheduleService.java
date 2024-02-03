package com.web.stard.domain.board.study.application;

import com.web.stard.domain.board.study.application.StudyService;
import com.web.stard.domain.board.study.domain.Study;
import com.web.stard.domain.board.study.domain.StudyMember;
import com.web.stard.domain.member.application.MemberService;
import com.web.stard.domain.member.domain.Member;
import com.web.stard.domain.board.study.domain.Schedule;
import com.web.stard.domain.board.study.repository.ScheduleRepository;
import com.web.stard.domain.board.study.repository.StudyMemberRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter @Setter
@AllArgsConstructor
@Service
public class ScheduleService {

    StudyService studyService;
    MemberService memberService;

    ScheduleRepository scheduleRepository;
    StudyMemberRepository studyMemberRepository;

    /* 일정 조회 (하나) */
    public Schedule getSchedule(Long id) {
        Optional<Schedule> result = scheduleRepository.findById(id);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    /* 사용자 일정 조회 (전체) */
    public List<Schedule> getAllScheduleListByMember(int year, int month, Authentication authentication) {
        Member member = memberService.find(authentication.getName());

        // TODO : 사용자가 참여중인 스터디의 모든 일정 가져오기 -> 동작 확인 필요
        List<StudyMember> studies = studyMemberRepository.findByMember(member);
        List<Schedule> schedules = new ArrayList<>();
        for (StudyMember sm : studies) {
            List<Schedule> scheduleList = getScheduleListByStudy(sm.getStudy().getId(), year, month);
            schedules.addAll(scheduleList);
        }

        return schedules;
    }

    /* 사용자 일정 조회 (스터디별) */
    public List<Schedule> getScheduleListByStudy(Long studyId, int year, int month) {
        Study study = studyService.findById(studyId);

        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime end = LocalDateTime.of(year, month, YearMonth.of(year, month).lengthOfMonth(),
                23, 59, 59);

        // 스터디의 모든 일정 가져오기
        List<Schedule> schedules = scheduleRepository.findAllByStudyAndStartDateBetween(study, start, end);

        return schedules;
    }

    /* 일정 등록 */
    public Schedule registerSchedule(Long studyId, Schedule schedule) {
        Study study = studyService.findById(studyId);
        schedule.setStudy(study);

        scheduleRepository.save(schedule);

        return schedule;
    }

    /* 일정 수정 (title, color만 변경) */
    public Schedule updateSchedule(Long scheduleId, String title, String color) {
        Schedule schedule = getSchedule(scheduleId);
        schedule.setTitle(title);
        schedule.setColor(color);

        scheduleRepository.save(schedule);

        return schedule;
    }

    /* 일정 삭제 */
    public boolean deleteSchedule(Long scheduleId) {
        Schedule schedule = getSchedule(scheduleId);
        scheduleRepository.delete(schedule);

        if (getSchedule(scheduleId) == null) { // 삭제됨
            return true;
        } return false;
    }

    /* 스터디원인지 확인 */
    public boolean checkStudyMemberBySchedule(Long scheduleId, String id) {
        Schedule schedule = getSchedule(scheduleId);
        return studyService.checkStudyMember(schedule.getStudy().getId(), id);
    }
}
