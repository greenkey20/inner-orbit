package com.greenkey20.innerorbit.weeklyreport.infrastructure.adapter.in.scheduler;

import com.greenkey20.innerorbit.weeklyreport.application.service.WeeklyReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 주간 리포트 자동 생성 스케줄러
 * 기본: 매주 일요일 자정 (0 0 0 * * SUN) — 완료된 한 주(일~토) 집계
 * 로컬 테스트: WEEKLY_REPORT_CRON 환경변수로 오버라이드 가능
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WeeklyReportScheduler {

    private final WeeklyReportService weeklyReportService;

    @Scheduled(cron = "${weekly-report.scheduler.cron}")
    public void generateWeeklyReports() {
        log.info("Weekly report scheduler triggered");
        LocalDate weekEnd   = LocalDate.now().minusDays(1);   // 어제 = 토요일
        LocalDate weekStart = weekEnd.minusDays(6);           // 6일 전 = 일요일 (일~토 한 주)
        weeklyReportService.generateForAllUsers(weekStart, weekEnd);
    }
}
