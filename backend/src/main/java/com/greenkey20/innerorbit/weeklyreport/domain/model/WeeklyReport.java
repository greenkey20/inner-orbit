package com.greenkey20.innerorbit.weeklyreport.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * WeeklyReport 순수 도메인 모델 (JPA 어노테이션 없음)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyReport {

    private Long id;
    private Long userId;
    private LocalDate weekStart;
    private LocalDate weekEnd;
    private WeeklyReportStatus status;
    private Integer logCount;
    private WeeklyReportContent report;
    private LocalDateTime createdAt;
}
