package com.greenkey20.innerorbit.domain.dto.response;

import lombok.*;

import java.util.List;

/**
 * AI 키워드 추천 응답 DTO
 * AI가 추천한 CS 개념 키워드 목록을 반환합니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KeywordSuggestionResponse {

    private List<String> keywords;

    /**
     * 추천된 키워드 개수
     */
    public int getCount() {
        return keywords != null ? keywords.size() : 0;
    }
}
