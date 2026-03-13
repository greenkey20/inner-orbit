package com.greenkey20.innerorbit.log.application.port.out;

import java.util.Map;

/**
 * AI 분석 out port — log 도메인에서 ai 도메인 호출 추상화
 */
public interface AiAnalysisPort {

    /**
     * 인지적 왜곡 분석 결과를 Map으로 반환 (JSONB 저장 정합)
     */
    Map<String, Object> analyzeCognitiveDistortions(String content, Integer gravity, Integer stability);

    /**
     * Insight Log AI 피드백 생성
     *
     * @param recentLogsContext 최근 Flight Log 컨텍스트 문자열
     */
    String generateInsightFeedback(String trigger, String abstraction, String application, String recentLogsContext);
}
