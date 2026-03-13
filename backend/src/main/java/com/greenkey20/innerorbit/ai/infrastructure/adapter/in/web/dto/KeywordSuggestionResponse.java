package com.greenkey20.innerorbit.ai.infrastructure.adapter.in.web.dto;

import lombok.*;

import java.util.List;

/**
 * AI 키워드 추천 응답 DTO
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeywordSuggestionResponse {

    private List<String> keywords;

    public int getCount() {
        return keywords != null ? keywords.size() : 0;
    }
}
