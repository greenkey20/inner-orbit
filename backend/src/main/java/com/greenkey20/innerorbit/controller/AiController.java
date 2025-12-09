package com.greenkey20.innerorbit.controller;

import com.greenkey20.innerorbit.domain.dto.request.AnalysisUpdateRequest;
import com.greenkey20.innerorbit.domain.dto.response.LogEntryResponse;
import com.greenkey20.innerorbit.service.AiService;
import com.greenkey20.innerorbit.service.LogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AiController - AI 관련 REST API 컨트롤러
 * 모든 AI 기반 기능(분석, 프롬프트 생성)을 처리합니다.
 */
@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AiController {

    private final AiService aiService;
    private final LogService logService;

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
     * AI를 사용하여 로그 엔트리 분석 실행
     *
     * POST /api/ai/analyze/{logId}
     *
     * @param logId 분석할 로그 엔트리 ID
     * @return 분석 결과가 포함된 로그 엔트리 응답
     */
    @PostMapping("/analyze/{logId}")
    public ResponseEntity<LogEntryResponse> analyzeLogEntry(@PathVariable Long logId) {
        log.info("Triggering AI analysis for log entry with id: {}", logId);
        LogEntryResponse response = logService.updateLogAnalysis(logId);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그 엔트리에 AI 분석 결과 수동 추가/업데이트
     *
     * PATCH /api/ai/analysis/{logId}
     *
     * @param logId 로그 엔트리 ID
     * @param request 분석 결과 데이터
     * @return 업데이트된 로그 엔트리 응답
     */
    @PatchMapping("/analysis/{logId}")
    public ResponseEntity<LogEntryResponse> updateAnalysis(
            @PathVariable Long logId,
            @Valid @RequestBody AnalysisUpdateRequest request) {
        log.info("Updating analysis for log entry with id: {}", logId);
        LogEntryResponse response = logService.updateAnalysis(logId, request);
        return ResponseEntity.ok(response);
    }
}
