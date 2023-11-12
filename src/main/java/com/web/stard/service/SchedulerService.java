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
}
