package com.greenkey20.innerorbit.service;

import com.greenkey20.innerorbit.domain.dto.request.AnalysisUpdateRequest;
import com.greenkey20.innerorbit.domain.dto.request.LogEntryCreateRequest;
import com.greenkey20.innerorbit.domain.dto.request.LogEntryUpdateRequest;
import com.greenkey20.innerorbit.domain.dto.response.LogEntryResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * LogService 인터페이스
 * 로그 엔트리 관련 비즈니스 로직을 정의
 */
public interface LogService {

    /**
     * 새로운 로그 엔트리 생성
     *
     * @param request 생성 요청 DTO
     * @return 생성된 로그 엔트리 응답 DTO
     */
    LogEntryResponse createLogEntry(LogEntryCreateRequest request);

    /**
     * 특정 로그 엔트리 조회
     *
     * @param id 조회할 엔트리 ID
     * @return 로그 엔트리 응답 DTO
     */
    LogEntryResponse getLogEntry(Long id);

    /**
     * 모든 로그 엔트리 조회 (최신순)
     *
     * @return 로그 엔트리 응답 DTO 리스트
     */
    List<LogEntryResponse> getAllLogEntries();

    /**
     * 특정 사용자의 모든 로그 엔트리 조회 (최신순)
     *
     * @param userId 사용자 ID
     * @return 로그 엔트리 응답 DTO 리스트
     */
    List<LogEntryResponse> getLogEntriesByUserId(Long userId);

    /**
     * 특정 기간 내의 로그 엔트리 조회
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @return 로그 엔트리 응답 DTO 리스트
     */
    List<LogEntryResponse> getLogEntriesByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 안정성 범위로 로그 엔트리 조회
     *
     * @param minStability 최소 안정성 값
     * @param maxStability 최대 안정성 값
     * @return 로그 엔트리 응답 DTO 리스트
     */
    List<LogEntryResponse> getLogEntriesByStabilityRange(Integer minStability, Integer maxStability);

    /**
     * AI 분석 결과가 있는 로그 엔트리만 조회
     *
     * @return 로그 엔트리 응답 DTO 리스트
     */
    List<LogEntryResponse> getLogEntriesWithAnalysis();

    /**
     * 로그 엔트리 수정
     *
     * @param id 수정할 엔트리 ID
     * @param request 수정 요청 DTO
     * @return 수정된 로그 엔트리 응답 DTO
     */
    LogEntryResponse updateLogEntry(Long id, LogEntryUpdateRequest request);

    /**
     * 로그 엔트리에 AI 분석 결과 추가/업데이트
     *
     * @param id 엔트리 ID
     * @param request 분석 결과 업데이트 요청 DTO
     * @return 업데이트된 로그 엔트리 응답 DTO
     */
    LogEntryResponse updateAnalysis(Long id, AnalysisUpdateRequest request);

    /**
     * AI를 사용하여 로그 엔트리 분석 및 결과 저장
     * 사용자의 로그 내용을 OpenAI GPT로 분석하고 인지적 왜곡 결과를 DB에 저장
     *
     * @param logId 분석할 로그 엔트리 ID
     * @return 분석이 완료된 로그 엔트리 응답 DTO
     */
    LogEntryResponse updateLogAnalysis(Long logId);

    /**
     * 로그 엔트리 삭제
     *
     * @param id 삭제할 엔트리 ID
     */
    void deleteLogEntry(Long id);

    /**
     * 전체 로그 엔트리 통계 조회
     *
     * @return 통계 데이터 (평균 안정성, 평균 gravity, 총 엔트리 수 등)
     */
    Map<String, Object> getStatistics();

    /**
     * 모든 로그 엔트리를 JSON 형식으로 내보내기
     *
     * @return 백업용 JSON 데이터
     */
    List<LogEntryResponse> exportAllEntries();

    /**
     * JSON 데이터에서 로그 엔트리 가져오기 (병합)
     *
     * @param entries 가져올 엔트리 리스트
     * @return 가져온 엔트리 응답 DTO 리스트
     */
    List<LogEntryResponse> importEntries(List<LogEntryCreateRequest> entries);
}
