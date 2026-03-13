package com.greenkey20.innerorbit.log.infrastructure.adapter.in.web;

import com.greenkey20.innerorbit.log.application.port.in.LogUseCase;
import com.greenkey20.innerorbit.log.infrastructure.adapter.in.web.dto.request.AnalysisUpdateRequest;
import com.greenkey20.innerorbit.log.infrastructure.adapter.in.web.dto.request.LogEntryCreateRequest;
import com.greenkey20.innerorbit.log.infrastructure.adapter.in.web.dto.request.LogEntryUpdateRequest;
import com.greenkey20.innerorbit.log.infrastructure.adapter.in.web.dto.response.LogEntryResponse;
import com.greenkey20.innerorbit.security.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    private final LogUseCase logUseCase;

    @PostMapping
    public ResponseEntity<LogEntryResponse> createLogEntry(
            @Valid @RequestBody LogEntryCreateRequest request,
            Authentication authentication) {
        log.info("Creating new log entry");
        Long userId = ((UserPrincipal) authentication.getPrincipal()).userId();
        LogEntryResponse response = logUseCase.createLogEntry(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LogEntryResponse> getLogEntry(@PathVariable Long id) {
        log.info("Fetching log entry with id: {}", id);
        LogEntryResponse response = logUseCase.getLogEntry(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<LogEntryResponse>> getAllLogEntries(Authentication authentication) {
        log.info("Fetching all log entries");
        Long userId = ((UserPrincipal) authentication.getPrincipal()).userId();
        List<LogEntryResponse> responses = logUseCase.getAllLogEntries(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/range")
    public ResponseEntity<List<LogEntryResponse>> getLogEntriesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("Fetching log entries between {} and {}", startDate, endDate);
        List<LogEntryResponse> responses = logUseCase.getLogEntriesByDateRange(startDate, endDate);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/stability")
    public ResponseEntity<List<LogEntryResponse>> getLogEntriesByStabilityRange(
            @RequestParam Integer min,
            @RequestParam Integer max) {
        log.info("Fetching log entries with stability between {} and {}", min, max);
        List<LogEntryResponse> responses = logUseCase.getLogEntriesByStabilityRange(min, max);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/analyzed")
    public ResponseEntity<List<LogEntryResponse>> getLogEntriesWithAnalysis() {
        log.info("Fetching log entries with analysis");
        List<LogEntryResponse> responses = logUseCase.getLogEntriesWithAnalysis();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LogEntryResponse> updateLogEntry(
            @PathVariable Long id,
            @Valid @RequestBody LogEntryUpdateRequest request) {
        log.info("Updating log entry with id: {}", id);
        LogEntryResponse response = logUseCase.updateLogEntry(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/analysis")
    public ResponseEntity<LogEntryResponse> updateAnalysis(
            @PathVariable Long id,
            @Valid @RequestBody AnalysisUpdateRequest request) {
        log.info("Updating analysis for log entry with id: {}", id);
        LogEntryResponse response = logUseCase.updateAnalysis(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/analyze")
    public ResponseEntity<LogEntryResponse> analyzeLogEntry(@PathVariable Long id) {
        log.info("Triggering AI analysis for log entry with id: {}", id);
        LogEntryResponse response = logUseCase.updateLogAnalysis(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/request-feedback")
    public ResponseEntity<LogEntryResponse> requestInsightFeedback(@PathVariable Long id) {
        log.info("Requesting AI feedback for Insight log with id: {}", id);
        LogEntryResponse response = logUseCase.generateInsightFeedback(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLogEntry(@PathVariable Long id) {
        log.info("Deleting log entry with id: {}", id);
        logUseCase.deleteLogEntry(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        log.info("Fetching log entry statistics");
        Map<String, Object> statistics = logUseCase.getStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/export")
    public ResponseEntity<List<LogEntryResponse>> exportAllEntries() {
        log.info("Exporting all log entries");
        List<LogEntryResponse> responses = logUseCase.exportAllEntries();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/import")
    public ResponseEntity<List<LogEntryResponse>> importEntries(
            @Valid @RequestBody List<LogEntryCreateRequest> entries) {
        log.info("Importing {} log entries", entries.size());
        List<LogEntryResponse> responses = logUseCase.importEntries(entries);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }
}
