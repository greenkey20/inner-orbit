package com.greenkey20.innerorbit.service.impl;

import com.greenkey20.innerorbit.domain.dto.request.AnalysisUpdateRequest;
import com.greenkey20.innerorbit.domain.dto.request.LogEntryCreateRequest;
import com.greenkey20.innerorbit.domain.dto.request.LogEntryUpdateRequest;
import com.greenkey20.innerorbit.domain.dto.response.LogEntryResponse;
import com.greenkey20.innerorbit.domain.entity.LogEntry;
import com.greenkey20.innerorbit.domain.dto.response.AnalysisResult;
import com.greenkey20.innerorbit.exception.BusinessException;
import com.greenkey20.innerorbit.exception.ErrorCode;
import com.greenkey20.innerorbit.repository.LogRepository;
import com.greenkey20.innerorbit.service.AiService;
import com.greenkey20.innerorbit.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * LogService 구현체
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;
    private final AiService aiService;

    @Override
    @Transactional
    public LogEntryResponse createLogEntry(LogEntryCreateRequest request) {
        // 1. Null 체크
        if (request == null) {
            throw new BusinessException(
                    ErrorCode.INVALID_INPUT_VALUE,
                    "요청 데이터가 null입니다."
            );
        }

        // 2. 비즈니스 로직 유효성 검증
        validateLogEntryRequest(request);

        // 3. DTO -> Entity 변환
        LogEntry logEntry = LogEntry.builder()
                .content(request.getContent())
                .stability(request.getStability())
                .gravity(request.getGravity())
                .location(request.getLocation())
                .sensoryVisual(request.getSensoryVisual())
                .sensoryAuditory(request.getSensoryAuditory())
                .sensoryTactile(request.getSensoryTactile())
                .isDeepLog(request.getIsDeepLog() != null ? request.getIsDeepLog() : false)
                .build();

        // 4. 엔티티 저장 (createdAt은 @PrePersist에서 자동 설정)
        LogEntry savedEntry = logRepository.save(logEntry);

        // 5. 로그 기록
        log.info("New log entry created - ID: {}, Stability: {}, Gravity: {}",
                savedEntry.getId(), savedEntry.getStability(), savedEntry.getGravity());

        // 6. Entity -> DTO 변환 후 반환
        return LogEntryResponse.from(savedEntry);
    }

    /**
     * 로그 엔트리 생성 요청 유효성 검증
     * (컨트롤러의 @Valid 외에 서비스 레벨에서 추가 비즈니스 검증)
     */
    private void validateLogEntryRequest(LogEntryCreateRequest request) {
        // 내용 검증
        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_CONTENT);
        }

        // 안정성 값 검증
        if (request.getStability() == null || request.getStability() < 0 || request.getStability() > 100) {
            throw new BusinessException(ErrorCode.INVALID_STABILITY_VALUE);
        }

        // 그리움 강도 검증
        if (request.getGravity() == null || request.getGravity() < 0 || request.getGravity() > 100) {
            throw new BusinessException(ErrorCode.INVALID_GRAVITY_VALUE);
        }
    }

    @Override
    public LogEntryResponse getLogEntry(Long id) {
        log.info("Fetching log entry with id: {}", id);

        LogEntry logEntry = logRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.LOG_ENTRY_NOT_FOUND,
                        String.format("ID %d에 해당하는 로그를 찾을 수 없습니다.", id)
                ));

        return LogEntryResponse.from(logEntry);
    }

    @Override
    public List<LogEntryResponse> getAllLogEntries() {
        log.info("Fetching all log entries");

        // [FIX] findAll() -> findAllByOrderByCreatedAtDesc()로 변경하여 최신순 정렬 보장
        List<LogEntry> entries = logRepository.findAllByOrderByCreatedAtDesc();

        return entries.stream()
                .map(LogEntryResponse::from)
                .toList();
    }

    @Override
    public List<LogEntryResponse> getLogEntriesByUserId(Long userId) {
        // TODO: 비즈니스 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LogEntryResponse> getLogEntriesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        // TODO: 비즈니스 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LogEntryResponse> getLogEntriesByStabilityRange(Integer minStability, Integer maxStability) {
        // TODO: 비즈니스 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LogEntryResponse> getLogEntriesWithAnalysis() {
        // TODO: 비즈니스 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public LogEntryResponse updateLogEntry(Long id, LogEntryUpdateRequest request) {
        log.info("Updating log entry id={} with request: {}", id, request);

        // 1. 기존 엔트리 조회
        LogEntry logEntry = logRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("로그를 찾을 수 없습니다. id: " + id));

        // 2. 필드 업데이트
        logEntry.setContent(request.getContent());
        logEntry.setStability(request.getStability());
        logEntry.setGravity(request.getGravity());

        // Deep Log 필드 업데이트 (null 허용)
        logEntry.setLocation(request.getLocation());
        logEntry.setSensoryVisual(request.getSensoryVisual());
        logEntry.setSensoryAuditory(request.getSensoryAuditory());
        logEntry.setSensoryTactile(request.getSensoryTactile());

        if (request.getIsDeepLog() != null) {
            logEntry.setIsDeepLog(request.getIsDeepLog());
        }

        // 3. 저장 (updatedAt은 @PreUpdate로 자동 설정됨)
        LogEntry updated = logRepository.save(logEntry);

        // 4. DTO로 변환하여 반환
        return LogEntryResponse.from(updated);
    }

    @Override
    @Transactional
    public LogEntryResponse updateAnalysis(Long id, AnalysisUpdateRequest request) {
        // TODO: 비즈니스 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public void deleteLogEntry(Long id) {
        // TODO: 비즈니스 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Map<String, Object> getStatistics() {
        // TODO: 비즈니스 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LogEntryResponse> exportAllEntries() {
        // TODO: 비즈니스 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public List<LogEntryResponse> importEntries(List<LogEntryCreateRequest> entries) {
        // TODO: 비즈니스 로직 구현
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public LogEntryResponse updateLogAnalysis(Long logId) {
        log.info("Starting AI analysis for log entry ID: {}", logId);

        // 1. 로그 엔트리 조회
        LogEntry logEntry = logRepository.findById(logId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.LOG_ENTRY_NOT_FOUND,
                        String.format("ID %d에 해당하는 로그를 찾을 수 없습니다.", logId)
                ));

        // 2. AI 서비스를 통해 인지적 왜곡 분석
        AnalysisResult analysisResult = aiService.analyzeCognitiveDistortions(
                logEntry.getContent(),
                logEntry.getGravity(),
                logEntry.getStability()
        );

        // 3. 분석 결과를 Map으로 변환하여 JSONB 필드에 저장
        Map<String, Object> analysisMap = convertAnalysisResultToMap(analysisResult);
        logEntry.setAnalysisResult(analysisMap);

        // 4. 엔티티 저장 (updatedAt은 @PreUpdate에서 자동 설정)
        LogEntry updatedEntry = logRepository.save(logEntry);

        log.info("AI analysis completed and saved for log entry ID: {}", logId);

        // 5. Entity -> DTO 변환 후 반환
        return LogEntryResponse.from(updatedEntry);
    }

    /**
     * AnalysisResult를 Map으로 변환하는 헬퍼 메서드
     */
    private Map<String, Object> convertAnalysisResultToMap(AnalysisResult result) {
        return Map.of(
                "distortions", result.getDistortions() != null ? result.getDistortions() : List.of(),
                "reframed", result.getReframed() != null ? result.getReframed() : "",
                "alternative", result.getAlternative() != null ? result.getAlternative() : ""
        );
    }
}
