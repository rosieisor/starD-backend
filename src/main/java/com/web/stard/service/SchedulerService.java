package com.web.stard.service;

import com.web.stard.domain.Study;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulerService {

    private final StudyService studyService;
    private final ReportService reportService;

//    매일 00시 스터디 모집 / 진행 상태 Update
//    @Scheduled(fixedDelay = 10000) => 10초 마다 실행
    @Scheduled(cron = "0 0 0 * * *")
    public void updateStudyState() {
        log.info("매일 자정 스터디 모집 및 진행 상태 관련 스케쥴러 실행");
        studyService.checkStudyActivityDeadline();
//        studyService.checkStudyActivityStart();
        studyService.checkStudyRecruitmentDeadline();
        log.info("매일 자정 스터디 모집 및 진행 상태 관련 스케쥴러 실행 완료");
    }

    // 누적 신고 횟수 10회 이상이면 자동으로 강제 탈퇴 처리
    @Scheduled(cron = "0 0/12 * * * ?")   // 매일 자정, 정오에 실행
//    @Scheduled(cron = "0 0/1 * * * ?") // 매 1분
    public void autoForceDeleteReportedMembers() {
        try {
            log.info("스케줄된 작업 시작: 회원 자동 탈퇴.");
            reportService.forceDeleteMember();
            log.info("스케줄된 작업 완료: 회원 자동 탈퇴.");
        } catch (Exception e) {
            log.error("스케줄된 작업 중 오류 발생: 회원 자동 탈퇴.", e);
        }
    }
}
