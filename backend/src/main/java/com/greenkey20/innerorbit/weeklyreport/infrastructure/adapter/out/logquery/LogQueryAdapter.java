package com.greenkey20.innerorbit.weeklyreport.infrastructure.adapter.out.logquery;

import com.greenkey20.innerorbit.log.domain.model.LogEntry;
import com.greenkey20.innerorbit.log.domain.model.LogType;
import com.greenkey20.innerorbit.log.infrastructure.adapter.out.persistence.LogJpaRepository;
import com.greenkey20.innerorbit.log.infrastructure.adapter.out.persistence.entity.LogEntryJpaEntity;
import com.greenkey20.innerorbit.weeklyreport.application.port.out.LogQueryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LogQueryPort 구현체 — weeklyreport 도메인이 log JPA 레포지토리를 직접 사용
 */
@Component
@RequiredArgsConstructor
public class LogQueryAdapter implements LogQueryPort {

    private final LogJpaRepository logJpaRepository;

    @Override
    public List<LogEntry> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime from, LocalDateTime to) {
        return logJpaRepository.findByUserIdAndCreatedAtBetween(userId, from, to)
                .stream().map(this::toDomainModel).toList();
    }

    private LogEntry toDomainModel(LogEntryJpaEntity entity) {
        return LogEntry.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .stability(entity.getStability())
                .gravity(entity.getGravity())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .analysisResult(entity.getAnalysisResult())
                .userId(entity.getUserId())
                .location(entity.getLocation())
                .sensoryVisual(entity.getSensoryVisual())
                .sensoryAuditory(entity.getSensoryAuditory())
                .sensoryTactile(entity.getSensoryTactile())
                .logType(entity.getLogType() != null ? entity.getLogType() : LogType.DAILY)
                .insightAbstraction(entity.getInsightAbstraction())
                .insightApplication(entity.getInsightApplication())
                .aiFeedback(entity.getAiFeedback())
                .build();
    }
}
