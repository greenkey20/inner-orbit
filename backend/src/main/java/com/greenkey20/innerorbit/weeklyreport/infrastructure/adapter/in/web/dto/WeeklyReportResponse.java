package com.greenkey20.innerorbit.weeklyreport.infrastructure.adapter.in.web.dto;

import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReport;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReportContent;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 주간 리포트 응답 DTO
 */
@Getter
@Builder
public class WeeklyReportResponse {

    private Long id;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private String status;
    private Integer logCount;
    private WeeklyReportContent report;
    private LocalDateTime createdAt;

    public static WeeklyReportResponse from(WeeklyReport weeklyReport) {
        return WeeklyReportResponse.builder()
                .id(weeklyReport.getId())
                .weekStart(weeklyReport.getWeekStart())
                .weekEnd(weeklyReport.getWeekEnd())
                .status(weeklyReport.getStatus().name())
                .logCount(weeklyReport.getLogCount())
                .report(weeklyReport.getReport())
                .createdAt(weeklyReport.getCreatedAt())
                .build();
    }
}
