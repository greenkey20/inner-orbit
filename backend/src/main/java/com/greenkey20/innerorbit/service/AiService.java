package com.greenkey20.innerorbit.service;

import com.greenkey20.innerorbit.domain.dto.response.AnalysisResult;

/**
 * AI 서비스 인터페이스
 * OpenAI GPT 모델을 사용한 감정 로그 분석 기능을 정의
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
}
