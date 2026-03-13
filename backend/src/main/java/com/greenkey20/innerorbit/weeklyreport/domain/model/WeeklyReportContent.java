package com.greenkey20.innerorbit.weeklyreport.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * AI가 생성한 주간 리포트 내용 — JSONB로 저장
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyReportContent {

    @JsonProperty("weeklyFlow")
    private String weeklyFlow;

    @JsonProperty("patterns")
    private String patterns;

    @JsonProperty("resilience")
    private String resilience;

    @JsonProperty("recommendations")
    private String recommendations;
}
