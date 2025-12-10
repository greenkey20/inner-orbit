package com.greenkey20.innerorbit.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.greenkey20.innerorbit.domain.entity.LogEntry;
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
                .build();
    }
}
