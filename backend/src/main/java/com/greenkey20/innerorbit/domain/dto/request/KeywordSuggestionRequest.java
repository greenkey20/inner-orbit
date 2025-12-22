package com.greenkey20.innerorbit.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * AI 키워드 추천 요청 DTO
 * Trigger(일상 관찰)를 받아서 연관된 CS 개념 키워드를 추천받습니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeywordSuggestionRequest {

    @NotBlank(message = "관찰 내용(Trigger)을 입력해주세요")
    @Size(max = 5000, message = "관찰 내용은 5000자를 초과할 수 없습니다")
    private String trigger;
}
