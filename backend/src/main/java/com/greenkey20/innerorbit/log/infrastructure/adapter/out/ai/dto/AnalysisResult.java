package com.greenkey20.innerorbit.log.infrastructure.adapter.out.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

/**
 * AI 인지적 왜곡 분석 결과 DTO
 * OpenAI GPT 모델의 분석 결과를 담는 클래스
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisResult {

    @JsonProperty("distortions")
    private List<DistortionDto> distortions;

    @JsonProperty("reframed")
    private String reframed;

    @JsonProperty("alternative")
    private String alternative;
}
