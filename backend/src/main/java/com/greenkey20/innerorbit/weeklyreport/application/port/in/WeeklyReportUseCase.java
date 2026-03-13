package com.greenkey20.innerorbit.weeklyreport.application.port.in;

import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReport;

import java.util.List;

/**
 * 주간 리포트 in port (사용자용 유스케이스)
 */
public interface WeeklyReportUseCase {

    List<WeeklyReport> getMyReports(Long userId);

    WeeklyReport getReportById(Long id, Long userId);

    WeeklyReport generateForCurrentWeek(Long userId);
}
