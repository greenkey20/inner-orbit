package com.greenkey20.innerorbit.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 인지적 왜곡 DTO
 * 분석된 개별 인지적 왜곡 정보를 담는 클래스
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistortionDto {

    /**
     * 왜곡 유형 (예: "흑백논리", "Mind Reading", "과잉일반화" 등)
     */
    @JsonProperty("type")
    private String type;

    /**
     * 로그에서 발견된 실제 인용구
     */
    @JsonProperty("quote")
    private String quote;
}
