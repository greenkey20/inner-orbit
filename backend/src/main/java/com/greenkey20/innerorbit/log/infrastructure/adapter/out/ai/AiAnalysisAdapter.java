package com.greenkey20.innerorbit.log.infrastructure.adapter.out.ai;

import com.greenkey20.innerorbit.ai.application.port.in.AiUseCase;
import com.greenkey20.innerorbit.log.application.port.out.AiAnalysisPort;
import com.greenkey20.innerorbit.log.infrastructure.adapter.out.ai.dto.AnalysisResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * AiAnalysisPort 구현체 — ai 도메인의 AiUseCase 호출
 */
@Component
@RequiredArgsConstructor
public class AiAnalysisAdapter implements AiAnalysisPort {

    private final AiUseCase aiUseCase;

    @Override
    public Map<String, Object> analyzeCognitiveDistortions(String content, Integer gravity, Integer stability) {
        AnalysisResult result = aiUseCase.analyzeCognitiveDistortions(content, gravity, stability);
        return convertAnalysisResultToMap(result);
    }

    @Override
    public String generateInsightFeedback(String trigger, String abstraction, String application, String recentLogsContext) {
        return aiUseCase.generateInsightFeedback(trigger, abstraction, application, recentLogsContext);
    }

    private Map<String, Object> convertAnalysisResultToMap(AnalysisResult result) {
        return Map.of(
                "distortions", result.getDistortions() != null ? result.getDistortions() : List.of(),
                "reframed", result.getReframed() != null ? result.getReframed() : "",
                "alternative", result.getAlternative() != null ? result.getAlternative() : ""
        );
    }
}
