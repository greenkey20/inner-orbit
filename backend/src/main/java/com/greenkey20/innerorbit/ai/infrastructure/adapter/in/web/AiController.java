package com.greenkey20.innerorbit.ai.infrastructure.adapter.in.web;

import com.greenkey20.innerorbit.ai.application.port.in.AiUseCase;
import com.greenkey20.innerorbit.ai.infrastructure.adapter.in.web.dto.KeywordSuggestionRequest;
import com.greenkey20.innerorbit.ai.infrastructure.adapter.in.web.dto.KeywordSuggestionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AiController - AI 관련 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AiController {

    private final AiUseCase aiUseCase;

    /**
     * GET /api/ai/prompt?gravity=50&stability=50
     */
    @GetMapping("/prompt")
    public ResponseEntity<Map<String, String>> generateDynamicPrompt(
            @RequestParam(defaultValue = "50") Integer gravity,
            @RequestParam(defaultValue = "50") Integer stability) {
        log.info("Generating dynamic prompt - Gravity: {}, Stability: {}", gravity, stability);

        try {
            String prompt = aiUseCase.generateDynamicPrompt(gravity, stability);
            return ResponseEntity.ok(Map.of("prompt", prompt));
        } catch (Exception e) {
            log.error("Failed to generate dynamic prompt: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "동적 프롬프트 생성에 실패했습니다."));
        }
    }

    /**
     * POST /api/ai/insights/suggest-keywords
     */
    @PostMapping("/insights/suggest-keywords")
    public ResponseEntity<?> suggestKeywords(@Valid @RequestBody KeywordSuggestionRequest request) {
        log.info("Suggesting CS keywords for trigger: {}", request.getTrigger().substring(0, Math.min(50, request.getTrigger().length())));

        try {
            List<String> keywords = aiUseCase.suggestCsKeywords(request.getTrigger());
            KeywordSuggestionResponse response = KeywordSuggestionResponse.builder()
                    .keywords(keywords)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to suggest keywords: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "키워드 추천에 실패했습니다."));
        }
    }
}
