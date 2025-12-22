package com.greenkey20.innerorbit.service;

import com.greenkey20.innerorbit.domain.dto.response.AnalysisResult;

import java.util.List;

/**
 * AI 서비스 인터페이스
 * OpenAI GPT 모델을 사용한 감정 로그 분석 및 Insight 지원 기능을 정의
 */
public interface AiService {

    /**
     * 인지적 왜곡 분석
     * 사용자의 로그 내용을 분석하여 인지적 왜곡을 탐지하고 재구성된 관점을 제공
     *
     * @param logContent 사용자가 작성한 로그 내용
     * @param gravity 외부 인력 (0-100)
     * @param stability 코어 안정성 (0-100)
     * @return 분석 결과 (인지적 왜곡 목록, 재구성된 관점, 대안적 관점)
     */
    AnalysisResult analyzeCognitiveDistortions(String logContent, Integer gravity, Integer stability);

    /**
     * 동적 프롬프트 생성
     * 사용자의 Gravity/Stability 상태를 기반으로 생각을 유도하는 질문을 생성
     *
     * @param gravity 외부 인력 (0-100)
     * @param stability 코어 안정성 (0-100)
     * @return 생성된 질문 (한국어)
     */
    String generateDynamicPrompt(Integer gravity, Integer stability);

    /**
     * Insight Log용 CS 키워드 추천
     * 일상 관찰(Trigger)을 분석하여 연관된 CS/소프트웨어 개념을 3-5개 제안
     *
     * @param trigger 사용자가 관찰한 일상적 현상
     * @return CS 개념 키워드 목록 (예: ["Message Queue", "Load Balancing", ...])
     */
    List<String> suggestCsKeywords(String trigger);

    /**
     * Insight Log에 대한 AI 피드백 생성
     * 사용자가 작성한 Insight를 분석하여 건설적인 피드백 제공
     *
     * @param trigger 관찰한 대상
     * @param abstraction 선택한 CS 개념
     * @param application 적용점
     * @return AI 피드백 (격려, 개선점, 추가 연결 개념 포함)
     */
    String generateInsightFeedback(String trigger, String abstraction, String application);
}
