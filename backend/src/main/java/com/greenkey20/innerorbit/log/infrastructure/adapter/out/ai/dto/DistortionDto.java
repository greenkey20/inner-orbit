package com.greenkey20.innerorbit.log.infrastructure.adapter.out.ai.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 인지적 왜곡 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistortionDto {

    @JsonProperty("type")
    private String type;

    @JsonProperty("quote")
    private String quote;
}
