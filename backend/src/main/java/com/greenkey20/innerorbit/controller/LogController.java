package com.greenkey20.innerorbit.controller;

import com.greenkey20.innerorbit.domain.dto.request.AnalysisUpdateRequest;
import com.greenkey20.innerorbit.domain.dto.request.LogEntryCreateRequest;
import com.greenkey20.innerorbit.domain.dto.request.LogEntryUpdateRequest;
import com.greenkey20.innerorbit.domain.dto.response.LogEntryResponse;
import com.greenkey20.innerorbit.service.LogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * LogController - 로그 엔트리 관련 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class LogController {

    private final LogService logService;

    /**
     * 새로운 로그 엔트리 생성
     *
     * POST /api/logs
     */
    @PostMapping
    public ResponseEntity<LogEntryResponse> createLogEntry(@Valid @RequestBody LogEntryCreateRequest request) {
        log.info("Creating new log entry");
        LogEntryResponse response = logService.createLogEntry(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 특정 로그 엔트리 조회
     *
     * GET /api/logs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<LogEntryResponse> getLogEntry(@PathVariable Long id) {
        log.info("Fetching log entry with id: {}", id);
        LogEntryResponse response = logService.getLogEntry(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 모든 로그 엔트리 조회 (최신순)
     *
     * GET /api/logs
     */
    @GetMapping
    public ResponseEntity<List<LogEntryResponse>> getAllLogEntries() {
        log.info("Fetching all log entries");
        List<LogEntryResponse> responses = logService.getAllLogEntries();
        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 사용자의 모든 로그 엔트리 조회
     *
     * GET /api/logs/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<LogEntryResponse>> getLogEntriesByUserId(@PathVariable Long userId) {
        log.info("Fetching log entries for user: {}", userId);
        List<LogEntryResponse> responses = logService.getLogEntriesByUserId(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 특정 기간 내의 로그 엔트리 조회
     *
     * GET /api/logs/range?startDate=2025-01-01T00:00:00&endDate=2025-12-31T23:59:59
     */
    @GetMapping("/range")
    public ResponseEntity<List<LogEntryResponse>> getLogEntriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching log entries between {} and {}", startDate, endDate);
        List<LogEntryResponse> responses = logService.getLogEntriesByDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    /**
     * 안정성 범위로 로그 엔트리 조회
     *
     * GET /api/logs/stability?min=0&max=50
     */
    @GetMapping("/stability")
    public ResponseEntity<List<LogEntryResponse>> getLogEntriesByStabilityRange(
            @RequestParam Integer min,
            @RequestParam Integer max) {
        log.info("Fetching log entries with stability between {} and {}", min, max);
        List<LogEntryResponse> responses = logService.getLogEntriesByStabilityRange(min, max);
        return ResponseEntity.ok(responses);
    }

    /**
     * AI 분석 결과가 있는 로그 엔트리만 조회
     *
     * GET /api/logs/analyzed
     */
    @GetMapping("/analyzed")
    public ResponseEntity<List<LogEntryResponse>> getLogEntriesWithAnalysis() {
        log.info("Fetching log entries with analysis");
        List<LogEntryResponse> responses = logService.getLogEntriesWithAnalysis();
        return ResponseEntity.ok(responses);
    }

    /**
     * 로그 엔트리 수정
     *
     * PUT /api/logs/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<LogEntryResponse> updateLogEntry(
            @PathVariable Long id,
            @Valid @RequestBody LogEntryUpdateRequest request) {
        log.info("Updating log entry with id: {}", id);
        LogEntryResponse response = logService.updateLogEntry(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그 엔트리에 AI 분석 결과 추가/업데이트
     *
     * PATCH /api/logs/{id}/analysis
     */
    @PatchMapping("/{id}/analysis")
    public ResponseEntity<LogEntryResponse> updateAnalysis(
            @PathVariable Long id,
            @Valid @RequestBody AnalysisUpdateRequest request) {
        log.info("Updating analysis for log entry with id: {}", id);
        LogEntryResponse response = logService.updateAnalysis(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * AI를 사용하여 로그 엔트리 분석 실행
     *
     * POST /api/logs/{id}/analyze
     */
    @PostMapping("/{id}/analyze")
    public ResponseEntity<LogEntryResponse> analyzeLogEntry(@PathVariable Long id) {
        log.info("Triggering AI analysis for log entry with id: {}", id);
        LogEntryResponse response = logService.updateLogAnalysis(id);
        return ResponseEntity.ok(response);
    }

    /**
     * 로그 엔트리 삭제
     *
     * DELETE /api/logs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLogEntry(@PathVariable Long id) {
        log.info("Deleting log entry with id: {}", id);
        logService.deleteLogEntry(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 전체 로그 엔트리 통계 조회
     *
     * GET /api/logs/statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("Fetching log entry statistics");
        Map<String, Object> statistics = logService.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * 모든 로그 엔트리 내보내기 (백업)
     *
     * GET /api/logs/export
     */
    @GetMapping("/export")
    public ResponseEntity<List<LogEntryResponse>> exportAllEntries() {
        log.info("Exporting all log entries");
        List<LogEntryResponse> responses = logService.exportAllEntries();
        return ResponseEntity.ok(responses);
    }

    /**
     * 로그 엔트리 가져오기 (복원/병합)
     *
     * POST /api/logs/import
     */
    @PostMapping("/import")
    public ResponseEntity<List<LogEntryResponse>> importEntries(
            @Valid @RequestBody List<LogEntryCreateRequest> entries) {
        log.info("Importing {} log entries", entries.size());
        List<LogEntryResponse> responses = logService.importEntries(entries);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}
