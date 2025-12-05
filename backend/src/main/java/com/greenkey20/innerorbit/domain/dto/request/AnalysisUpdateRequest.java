package com.greenkey20.innerorbit.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Map;

/**
 * AI 분석 결과 업데이트 요청 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisUpdateRequest {

    @NotNull(message = "분석 결과는 필수입니다")
    private Map<String, Object> analysisResult;
}
