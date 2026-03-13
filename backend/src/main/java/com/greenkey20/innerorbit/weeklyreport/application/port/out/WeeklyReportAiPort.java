package com.greenkey20.innerorbit.weeklyreport.application.port.out;

import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReportContent;

/**
 * 주간 리포트 AI 생성 out port
 */
public interface WeeklyReportAiPort {

    WeeklyReportContent generateWeeklyReport(String formattedLogs);
}
