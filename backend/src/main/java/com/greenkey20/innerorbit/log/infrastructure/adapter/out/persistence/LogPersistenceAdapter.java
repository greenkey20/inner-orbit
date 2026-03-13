package com.greenkey20.innerorbit.log.infrastructure.adapter.out.persistence;

import com.greenkey20.innerorbit.log.application.port.out.LogRepository;
import com.greenkey20.innerorbit.log.domain.model.LogEntry;
import com.greenkey20.innerorbit.log.domain.model.LogType;
import com.greenkey20.innerorbit.log.infrastructure.adapter.out.persistence.entity.LogEntryJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * LogRepository out port 구현체 — JPA 영속성 어댑터
 */
@Repository
@RequiredArgsConstructor
public class LogPersistenceAdapter implements LogRepository {

    private final LogJpaRepository logJpaRepository;

    @Override
    public LogEntry save(LogEntry logEntry) {
        LogEntryJpaEntity entity = toJpaEntity(logEntry);
        LogEntryJpaEntity saved = logJpaRepository.save(entity);
        return toDomainModel(saved);
    }

    @Override
    public Optional<LogEntry> findById(Long id) {
        return logJpaRepository.findById(id).map(this::toDomainModel);
    }

    @Override
    public List<LogEntry> findAllOrderByCreatedAtDesc() {
        return logJpaRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toDomainModel)
                .toList();
    }

    @Override
    public List<LogEntry> findTop5ByLogType(LogType logType) {
        return logJpaRepository.findTop5ByLogTypeOrderByCreatedAtDesc(logType).stream()
                .map(this::toDomainModel)
                .toList();
    }

    @Override
    public List<LogEntry> findAllByUserIdOrderByCreatedAtDesc(Long userId) {
        return logJpaRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream().map(this::toDomainModel).toList();
    }

    @Override
    public List<LogEntry> findTop5ByLogTypeAndUserId(LogType logType, Long userId) {
        return logJpaRepository.findTop5ByLogTypeAndUserIdOrderByCreatedAtDesc(logType, userId)
                .stream().map(this::toDomainModel).toList();
    }

    @Override
    public void flush() {
        logJpaRepository.flush();
    }

    private LogEntryJpaEntity toJpaEntity(LogEntry model) {
        return LogEntryJpaEntity.builder()
                .id(model.getId())
                .content(model.getContent())
                .stability(model.getStability())
                .gravity(model.getGravity())
                .createdAt(model.getCreatedAt())
                .updatedAt(model.getUpdatedAt())
                .analysisResult(model.getAnalysisResult())
                .userId(model.getUserId())
                .location(model.getLocation())
                .sensoryVisual(model.getSensoryVisual())
                .sensoryAuditory(model.getSensoryAuditory())
                .sensoryTactile(model.getSensoryTactile())
                .logType(model.getLogType() != null ? model.getLogType() : LogType.DAILY)
                .insightAbstraction(model.getInsightAbstraction())
                .insightApplication(model.getInsightApplication())
                .aiFeedback(model.getAiFeedback())
                .build();
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
                .logType(entity.getLogType())
                .insightAbstraction(entity.getInsightAbstraction())
                .insightApplication(entity.getInsightApplication())
                .aiFeedback(entity.getAiFeedback())
                .build();
    }
}
