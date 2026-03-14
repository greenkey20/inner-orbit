package com.greenkey20.innerorbit.weeklyreport.application.service;

import com.greenkey20.innerorbit.log.domain.model.LogEntry;
import com.greenkey20.innerorbit.log.domain.model.LogType;
import com.greenkey20.innerorbit.weeklyreport.application.port.out.LogQueryPort;
import com.greenkey20.innerorbit.weeklyreport.application.port.out.WeeklyReportAiPort;
import com.greenkey20.innerorbit.weeklyreport.application.port.out.WeeklyReportRepository;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReport;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReportContent;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReportStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자별 주간 리포트 생성 컴포넌트.
 * WeeklyReportService에서 분리한 이유: @Transactional self-invocation 방지
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WeeklyReportGenerator {

    private final LogQueryPort logQueryPort;
    private final WeeklyReportRepository weeklyReportRepository;
    private final WeeklyReportAiPort weeklyReportAiPort;

    @Transactional
    public WeeklyReport generateReportForUser(Long userId, LocalDate weekStart, LocalDate weekEnd) {
        LocalDateTime from = weekStart.atStartOfDay();
        LocalDateTime to = weekEnd.atTime(23, 59, 59);

        List<LogEntry> logs = logQueryPort.findByUserIdAndCreatedAtBetween(userId, from, to);
        int logCount = logs.size();

        WeeklyReportStatus status;
        WeeklyReportContent report = null;

        if (logCount == 0) {
            status = WeeklyReportStatus.NA;
        } else if (logCount == 1) {
            status = WeeklyReportStatus.INSUFFICIENT;
        } else {
            status = WeeklyReportStatus.GENERATED;
            String formattedLogs = formatLogsForAi(logs);
            report = weeklyReportAiPort.generateWeeklyReport(formattedLogs);
        }

        WeeklyReport weeklyReport = WeeklyReport.builder()
                .userId(userId)
                .weekStart(weekStart)
                .weekEnd(weekEnd)
                .status(status)
                .logCount(logCount)
                .report(report)
                .build();

        log.info("Saving weekly report for userId={}, weekStart={}, status={}", userId, weekStart, status);
        return weeklyReportRepository.save(weeklyReport);
    }

    private String formatLogsForAi(List<LogEntry> logs) {
        StringBuilder sb = new StringBuilder();
        for (LogEntry log : logs) {
            String dateStr = log.getCreatedAt().toLocalDate().toString();
            LogType type = log.getLogType() != null ? log.getLogType() : LogType.DAILY;

            switch (type) {
                case DAILY -> sb.append("[%s] 안정성:%d, 중력:%d / %s".formatted(
                        dateStr, log.getStability(), log.getGravity(), log.getContent()));
                case SENSORY -> sb.append("[%s] 장소:%s / 시각:%s, 청각:%s, 촉각:%s".formatted(
                        dateStr, log.getLocation(),
                        log.getSensoryVisual(), log.getSensoryAuditory(), log.getSensoryTactile()));
                case INSIGHT -> sb.append("[%s] 관찰:%s → CS개념:%s → 적용:%s".formatted(
                        dateStr, log.getContent(),
                        log.getInsightAbstraction(), log.getInsightApplication()));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
