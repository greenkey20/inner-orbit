package com.greenkey20.innerorbit.controller;

import com.greenkey20.innerorbit.service.AiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
