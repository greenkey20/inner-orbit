package com.greenkey20.innerorbit.weeklyreport.domain.model;

/**
 * 주간 리포트 상태
 * - NA: 해당 주에 로그 0건
 * - INSUFFICIENT: 로그 1건 (분석 불충분)
 * - GENERATED: 로그 2건 이상, AI 리포트 생성 완료
 */
public enum WeeklyReportStatus {
    NA,
    INSUFFICIENT,
    GENERATED
}
