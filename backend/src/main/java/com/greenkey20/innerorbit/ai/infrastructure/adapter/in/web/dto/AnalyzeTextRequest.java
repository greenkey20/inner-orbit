package com.greenkey20.innerorbit.ai.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * Stateless 텍스트 분석 요청 DTO — DB 저장 없이 AI 인지왜곡 분석
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyzeTextRequest {

    @NotBlank(message = "분석할 텍스트를 입력해주세요")
    @Size(max = 10000, message = "텍스트는 10000자를 초과할 수 없습니다")
    private String text;

    @Builder.Default
    private Integer gravity = 50;

    @Builder.Default
    private Integer stability = 50;
}
