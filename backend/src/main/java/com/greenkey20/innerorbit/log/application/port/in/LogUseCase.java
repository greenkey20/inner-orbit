package com.greenkey20.innerorbit.log.application.port.in;

import com.greenkey20.innerorbit.log.infrastructure.adapter.in.web.dto.request.AnalysisUpdateRequest;
import com.greenkey20.innerorbit.log.infrastructure.adapter.in.web.dto.request.LogEntryCreateRequest;
import com.greenkey20.innerorbit.log.infrastructure.adapter.in.web.dto.request.LogEntryUpdateRequest;
import com.greenkey20.innerorbit.log.infrastructure.adapter.in.web.dto.response.LogEntryResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 로그 도메인 in port (use case)
 */
public interface LogUseCase {

    LogEntryResponse createLogEntry(LogEntryCreateRequest request, Long userId);

    LogEntryResponse getLogEntry(Long id);

    List<LogEntryResponse> getAllLogEntries(Long userId);

    List<LogEntryResponse> getLogEntriesByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<LogEntryResponse> getLogEntriesByStabilityRange(Integer minStability, Integer maxStability);

    List<LogEntryResponse> getLogEntriesWithAnalysis();

    LogEntryResponse updateLogEntry(Long id, LogEntryUpdateRequest request);

    LogEntryResponse updateAnalysis(Long id, AnalysisUpdateRequest request);

    LogEntryResponse updateLogAnalysis(Long logId);

    LogEntryResponse generateInsightFeedback(Long logId);

    void deleteLogEntry(Long id);

    Map<String, Object> getStatistics();

    List<LogEntryResponse> exportAllEntries();

    List<LogEntryResponse> importEntries(List<LogEntryCreateRequest> entries);
}
