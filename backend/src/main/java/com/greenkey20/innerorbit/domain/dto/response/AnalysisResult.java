package com.greenkey20.innerorbit.domain.dto.response;

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

    /**
     * 발견된 인지적 왜곡 목록
     * 왜곡이 없을 경우 빈 리스트
     */
    @JsonProperty("distortions")
    private List<DistortionDto> distortions;

    /**
     * 재구성된 관점 (한국어, 2-3 문장)
     * 인지적 왜곡을 부드럽게 재해석한 내용
     */
    @JsonProperty("reframed")
    private String reframed;

    /**
     * 대안적 관점 (한국어, 1-2 문장)
     * 새로운 시각으로 상황을 바라볼 수 있는 제안
     */
    @JsonProperty("alternative")
    private String alternative;
}
