package com.greenkey20.innerorbit.log.domain.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * LogEntry 순수 도메인 모델 (JPA 어노테이션 없음)
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogEntry {

    private Long id;
    private String content;
    private Integer stability;
    private Integer gravity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Map<String, Object> analysisResult;
    private Long userId;
    private String location;
    private String sensoryVisual;
    private String sensoryAuditory;
    private String sensoryTactile;
    private LogType logType;
    private String insightAbstraction;
    private String insightApplication;
    private String aiFeedback;
}
