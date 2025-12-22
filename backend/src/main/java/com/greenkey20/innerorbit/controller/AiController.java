package com.greenkey20.innerorbit.controller;

import com.greenkey20.innerorbit.domain.dto.request.KeywordSuggestionRequest;
import com.greenkey20.innerorbit.domain.dto.response.KeywordSuggestionResponse;
import com.greenkey20.innerorbit.service.AiService;
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

    private final AiService aiService;

    /**
     * 동적 프롬프트 생성
     *
     * GET /api/ai/prompt?gravity=50&stability=50
     *
     * @param gravity 외부 인력 (0-100, 기본값: 50)
     * @param stability 코어 안정성 (0-100, 기본값: 50)
     * @return 생성된 프롬프트 (한국어)
     */
    @GetMapping("/prompt")
    public ResponseEntity<Map<String, String>> generateDynamicPrompt(
            @RequestParam(defaultValue = "50") Integer gravity,
            @RequestParam(defaultValue = "50") Integer stability) {
        log.info("Generating dynamic prompt - Gravity: {}, Stability: {}", gravity, stability);

        try {
            String prompt = aiService.generateDynamicPrompt(gravity, stability);
            return ResponseEntity.ok(Map.of("prompt", prompt));
        } catch (Exception e) {
            log.error("Failed to generate dynamic prompt: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "동적 프롬프트 생성에 실패했습니다."));
        }
    }

    /**
     * Insight Log용 CS 키워드 추천
     *
     * POST /api/ai/insights/suggest-keywords
     * Request Body: { "trigger": "일상 관찰 내용" }
     *
     * @param request 키워드 추천 요청 (trigger 포함)
     * @return 추천된 CS 개념 키워드 목록 (3-5개)
     */
    @PostMapping("/insights/suggest-keywords")
    public ResponseEntity<?> suggestKeywords(@Valid @RequestBody KeywordSuggestionRequest request) {
        log.info("Suggesting CS keywords for trigger: {}", request.getTrigger().substring(0, Math.min(50, request.getTrigger().length())));

        try {
            List<String> keywords = aiService.suggestCsKeywords(request.getTrigger());
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
