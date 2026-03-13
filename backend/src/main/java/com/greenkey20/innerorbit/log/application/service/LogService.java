package com.greenkey20.innerorbit.log.application.service;

import com.greenkey20.innerorbit.common.exception.BusinessException;
import com.greenkey20.innerorbit.common.exception.ErrorCode;
import com.greenkey20.innerorbit.log.application.port.in.LogUseCase;
import com.greenkey20.innerorbit.log.application.port.out.AiAnalysisPort;
import com.greenkey20.innerorbit.log.application.port.out.LogRepository;
import com.greenkey20.innerorbit.log.domain.model.LogEntry;
import com.greenkey20.innerorbit.log.domain.model.LogType;
import com.greenkey20.innerorbit.log.infrastructure.adapter.in.web.dto.request.AnalysisUpdateRequest;
import com.greenkey20.innerorbit.log.infrastructure.adapter.in.web.dto.request.LogEntryCreateRequest;
import com.greenkey20.innerorbit.log.infrastructure.adapter.in.web.dto.request.LogEntryUpdateRequest;
import com.greenkey20.innerorbit.log.infrastructure.adapter.in.web.dto.response.LogEntryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * LogUseCase 구현체 — log 도메인 application service
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class LogService implements LogUseCase {

    private final LogRepository logRepository;
    private final AiAnalysisPort aiAnalysisPort;

    @Override
    @Transactional
    public LogEntryResponse createLogEntry(LogEntryCreateRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "요청 데이터가 null입니다.");
        }

        validateLogEntryRequest(request);

        LogEntry logEntry = LogEntry.builder()
                .content(request.getContent())
                .stability(request.getStability())
                .gravity(request.getGravity())
                .location(request.getLocation())
                .sensoryVisual(request.getSensoryVisual())
                .sensoryAuditory(request.getSensoryAuditory())
                .sensoryTactile(request.getSensoryTactile())
                .logType(request.getLogType() != null ? request.getLogType() : LogType.DAILY)
                .insightAbstraction(request.getInsightAbstraction())
                .insightApplication(request.getInsightApplication())
                .aiFeedback(request.getAiFeedback())
                .build();

        LogEntry savedEntry = logRepository.save(logEntry);

        log.info("New log entry created - ID: {}, Stability: {}, Gravity: {}",
                savedEntry.getId(), savedEntry.getStability(), savedEntry.getGravity());

        return LogEntryResponse.from(savedEntry);
    }

    private void validateLogEntryRequest(LogEntryCreateRequest request) {
        LogType logType = request.getLogType() != null ? request.getLogType() : LogType.DAILY;

        if (request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_CONTENT);
        }

        if (logType == LogType.INSIGHT) {
            if (request.getInsightAbstraction() == null || request.getInsightAbstraction().trim().isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Insight Abstraction을 입력해주세요.");
            }
            if (request.getInsightApplication() == null || request.getInsightApplication().trim().isEmpty()) {
                throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Insight Application을 입력해주세요.");
            }
        }

        if (request.getStability() == null || request.getStability() < 0 || request.getStability() > 100) {
            throw new BusinessException(ErrorCode.INVALID_STABILITY_VALUE);
        }

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
        return logRepository.findAllOrderByCreatedAtDesc().stream()
                .map(LogEntryResponse::from)
                .toList();
    }

    @Override
    public List<LogEntryResponse> getLogEntriesByUserId(Long userId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LogEntryResponse> getLogEntriesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LogEntryResponse> getLogEntriesByStabilityRange(Integer minStability, Integer maxStability) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LogEntryResponse> getLogEntriesWithAnalysis() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public LogEntryResponse updateLogEntry(Long id, LogEntryUpdateRequest request) {
        log.info("Updating log entry id={} with request: {}", id, request);

        LogEntry logEntry = logRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("로그를 찾을 수 없습니다. id: " + id));

        logEntry.setContent(request.getContent());
        logEntry.setStability(request.getStability());
        logEntry.setGravity(request.getGravity());

        if (request.getLogType() != null) {
            logEntry.setLogType(request.getLogType());
        }

        logEntry.setLocation(request.getLocation());
        logEntry.setSensoryVisual(request.getSensoryVisual());
        logEntry.setSensoryAuditory(request.getSensoryAuditory());
        logEntry.setSensoryTactile(request.getSensoryTactile());
        logEntry.setInsightAbstraction(request.getInsightAbstraction());
        logEntry.setInsightApplication(request.getInsightApplication());
        logEntry.setAiFeedback(request.getAiFeedback());

        LogEntry updated = logRepository.save(logEntry);
        logRepository.flush();

        return LogEntryResponse.from(updated);
    }

    @Override
    @Transactional
    public LogEntryResponse updateAnalysis(Long id, AnalysisUpdateRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public void deleteLogEntry(Long id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Map<String, Object> getStatistics() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public List<LogEntryResponse> exportAllEntries() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public List<LogEntryResponse> importEntries(List<LogEntryCreateRequest> entries) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    @Transactional
    public LogEntryResponse updateLogAnalysis(Long logId) {
        log.info("Starting AI analysis for log entry ID: {}", logId);

        LogEntry logEntry = logRepository.findById(logId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.LOG_ENTRY_NOT_FOUND,
                        String.format("ID %d에 해당하는 로그를 찾을 수 없습니다.", logId)
                ));

        Map<String, Object> analysisMap = aiAnalysisPort.analyzeCognitiveDistortions(
                logEntry.getContent(),
                logEntry.getGravity(),
                logEntry.getStability()
        );
        logEntry.setAnalysisResult(analysisMap);

        LogEntry updatedEntry = logRepository.save(logEntry);
        log.info("AI analysis completed and saved for log entry ID: {}", logId);

        return LogEntryResponse.from(updatedEntry);
    }

    @Override
    @Transactional
    public LogEntryResponse generateInsightFeedback(Long logId) {
        log.info("Generating AI feedback for Insight log entry ID: {}", logId);

        LogEntry logEntry = logRepository.findById(logId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.LOG_ENTRY_NOT_FOUND,
                        String.format("ID %d에 해당하는 로그를 찾을 수 없습니다.", logId)
                ));

        if (logEntry.getContent() == null || logEntry.getContent().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "관찰 내용(content)이 없습니다.");
        }
        if (logEntry.getInsightAbstraction() == null || logEntry.getInsightAbstraction().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Insight Abstraction이 없습니다.");
        }
        if (logEntry.getInsightApplication() == null || logEntry.getInsightApplication().trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "Insight Application이 없습니다.");
        }

        String recentLogsContext = buildRecentFlightLogsContext();

        String feedback = aiAnalysisPort.generateInsightFeedback(
                logEntry.getContent(),
                logEntry.getInsightAbstraction(),
                logEntry.getInsightApplication(),
                recentLogsContext
        );

        logEntry.setAiFeedback(feedback);
        LogEntry updatedEntry = logRepository.save(logEntry);
        log.info("AI feedback generated and saved for Insight log entry ID: {}", logId);

        return LogEntryResponse.from(updatedEntry);
    }

    /**
     * 최근 Flight Log를 요약하여 컨텍스트 문자열 생성
     * Insight Feedback에 사용자의 최근 감정 패턴을 제공하기 위함
     */
    private String buildRecentFlightLogsContext() {
        try {
            List<LogEntry> recentLogs = logRepository.findTop5ByLogType(LogType.DAILY);

            if (recentLogs.isEmpty()) {
                return "";
            }

            StringBuilder context = new StringBuilder();
            context.append("\n[사용자의 최근 감정 패턴 (Flight Logs)]\n");

            for (int i = 0; i < recentLogs.size(); i++) {
                LogEntry logEntry = recentLogs.get(i);

                String contentSummary = logEntry.getContent().length() > 50
                        ? logEntry.getContent().substring(0, 50) + "..."
                        : logEntry.getContent();

                String emotionalState = String.format("(G:%d%%, S:%d%%)",
                        logEntry.getGravity(), logEntry.getStability());

                String distortionPattern = extractDistortionPattern(logEntry);

                context.append(String.format("%d. \"%s\" %s", i + 1, contentSummary, emotionalState));

                if (!distortionPattern.isEmpty()) {
                    context.append(" - ").append(distortionPattern);
                }

                context.append("\n");
            }

            log.debug("Recent Flight Logs context built: {} logs", recentLogs.size());
            return context.toString();

        } catch (Exception e) {
            log.warn("Failed to build recent logs context: {}", e.getMessage());
            return "";
        }
    }

    @SuppressWarnings("unchecked")
    private String extractDistortionPattern(LogEntry logEntry) {
        try {
            Map<String, Object> analysisResult = logEntry.getAnalysisResult();
            if (analysisResult == null || !analysisResult.containsKey("distortions")) {
                return "";
            }

            List<Map<String, String>> distortions =
                    (List<Map<String, String>>) analysisResult.get("distortions");

            if (distortions == null || distortions.isEmpty()) {
                return "";
            }

            List<String> types = distortions.stream()
                    .map(d -> d.get("type"))
                    .filter(type -> type != null && !type.isEmpty())
                    .toList();

            if (types.isEmpty()) {
                return "";
            }

            return String.join(", ", types) + " 감지";

        } catch (Exception e) {
            log.warn("Failed to extract distortion pattern: {}", e.getMessage());
            return "";
        }
    }
}
