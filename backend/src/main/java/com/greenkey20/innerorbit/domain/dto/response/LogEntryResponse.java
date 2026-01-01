package com.greenkey20.innerorbit.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenkey20.innerorbit.domain.entity.LogEntry;
import com.greenkey20.innerorbit.domain.entity.LogType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 로그 엔트리 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntryResponse {

    private Long id;
    private String content;
    private Integer stability;
    private Integer gravity;

    // [FIX] timezone을 명시하여 타임존 혼동 방지
    @JsonFormat(pattern = "yyyy년 M월 d일 HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy년 M월 d일 HH:mm", timezone = "Asia/Seoul")
    private LocalDateTime updatedAt;

    private Map<String, Object> analysisResult;
    private String location;
    private String sensoryVisual;
    private String sensoryAuditory;
    private String sensoryTactile;
    private LogType logType;
    private String insightAbstraction;
    private String insightApplication;
    private String aiFeedback;

    /**
     * Backward compatibility: computed from logType
     * @deprecated Use {@link #logType} instead
     */
    @Deprecated
    public Boolean getIsDeepLog() {
        return logType == LogType.SENSORY;
    }

    /**
     * Entity -> DTO 변환 메서드
     */
    public static LogEntryResponse from(LogEntry entity) {
        return LogEntryResponse.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .stability(entity.getStability())
                .gravity(entity.getGravity())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .analysisResult(entity.getAnalysisResult())
                .location(entity.getLocation())
                .sensoryVisual(entity.getSensoryVisual())
                .sensoryAuditory(entity.getSensoryAuditory())
                .sensoryTactile(entity.getSensoryTactile())
                .logType(entity.getLogType())
                .insightAbstraction(entity.getInsightAbstraction())
                .insightApplication(entity.getInsightApplication())
                .aiFeedback(entity.getAiFeedback())
                .build();
    }
}
