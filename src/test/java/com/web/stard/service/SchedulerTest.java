package com.web.stard.service;

import com.web.stard.domain.board.study.application.StudyService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SchedulerTest {

    @Autowired
    private StudyService studyService;

    @Test
    @DisplayName("모집 마감일, 시작일, 마감일 지난 것들 추출")
    public void test() {
        System.out.printf("모집 마감일 지난 것들 추출");
        studyService.checkStudyRecruitmentDeadline();

        System.out.printf("스터디 마감일 지난 것들 추출");
        studyService.checkStudyActivityDeadline();
    }
}