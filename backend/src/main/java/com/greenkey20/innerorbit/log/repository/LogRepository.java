package com.greenkey20.innerorbit.log.repository;

import com.greenkey20.innerorbit.log.domain.entity.LogEntry;
import com.greenkey20.innerorbit.log.domain.entity.LogType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LogEntry 레포지토리 인터페이스
 */
@Repository
public interface LogRepository extends JpaRepository<LogEntry, Long> {

    /**
     * 생성 날짜를 기준으로 내림차순 정렬하여 모든 엔트리 조회
     */
    List<LogEntry> findAllByOrderByCreatedAtDesc();

    /**
     * 특정 사용자의 모든 엔트리를 생성 날짜 기준 내림차순으로 조회
     */
    List<LogEntry> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 특정 기간 내의 엔트리 조회
     */
    List<LogEntry> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * 안정성 값이 특정 범위 내에 있는 엔트리 조회
     */
    List<LogEntry> findByStabilityBetweenOrderByCreatedAtDesc(Integer minStability, Integer maxStability);

    /**
     * 분석 결과가 있는 엔트리만 조회
     */
    List<LogEntry> findByAnalysisResultIsNotNullOrderByCreatedAtDesc();

    /**
     * 특정 타입의 최근 N개 로그 조회 (Issue #30 - 컨텍스트 추가)
     * @param logType 로그 타입 (DAILY, INSIGHT, SENSORY)
     * @return 최근 생성된 순으로 정렬된 로그 리스트
     */
    List<LogEntry> findTop5ByLogTypeOrderByCreatedAtDesc(LogType logType);
}
