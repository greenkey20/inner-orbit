package com.greenkey20.innerorbit.log.infrastructure.adapter.in.web.dto.response;

import com.greenkey20.innerorbit.log.domain.model.LogEntry;
import com.greenkey20.innerorbit.log.domain.model.LogType;
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
    private LocalDateTime createdAt;
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
     * 도메인 모델 -> DTO 변환
     */
    public static LogEntryResponse from(LogEntry model) {
        return LogEntryResponse.builder()
                .id(model.getId())
                .content(model.getContent())
                .stability(model.getStability())
                .gravity(model.getGravity())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .analysisResult(model.getAnalysisResult())
                .location(model.getLocation())
                .sensoryVisual(model.getSensoryVisual())
                .sensoryAuditory(model.getSensoryAuditory())
                .sensoryTactile(model.getSensoryTactile())
                .logType(model.getLogType())
                .insightAbstraction(model.getInsightAbstraction())
                .insightApplication(model.getInsightApplication())
                .aiFeedback(model.getAiFeedback())
                .build();
    }
}
