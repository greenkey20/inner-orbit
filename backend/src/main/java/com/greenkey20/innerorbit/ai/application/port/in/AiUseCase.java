package com.greenkey20.innerorbit.ai.application.port.in;

import com.greenkey20.innerorbit.log.infrastructure.adapter.out.ai.dto.AnalysisResult;

import java.util.List;

/**
 * AI 도메인 in port (use case)
 */
public interface AiUseCase {

    /**
     * 인지적 왜곡 분석
     */
    AnalysisResult analyzeCognitiveDistortions(String logContent, Integer gravity, Integer stability);

    /**
     * 동적 프롬프트 생성
     */
    String generateDynamicPrompt(Integer gravity, Integer stability);

    /**
     * Insight Log용 CS 키워드 추천
     */
    List<String> suggestCsKeywords(String trigger);

    /**
     * Insight Log에 대한 AI 피드백 생성
     *
     * @param recentLogsContext 최근 Flight Log 컨텍스트 (log 도메인에서 빌드)
     */
    String generateInsightFeedback(String trigger, String abstraction, String application, String recentLogsContext);

    /**
     * DB 저장 없는 Stateless 텍스트 분석 — 인지왜곡 분석 결과만 반환
     */
    AnalysisResult analyzeTextOnly(String text, Integer gravity, Integer stability);
}
