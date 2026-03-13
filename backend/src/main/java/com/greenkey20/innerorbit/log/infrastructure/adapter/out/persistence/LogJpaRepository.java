package com.greenkey20.innerorbit.log.infrastructure.adapter.out.persistence;

import com.greenkey20.innerorbit.log.domain.model.LogType;
import com.greenkey20.innerorbit.log.infrastructure.adapter.out.persistence.entity.LogEntryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * LogEntry Spring Data JPA 레포지토리
 */
@Repository
public interface LogJpaRepository extends JpaRepository<LogEntryJpaEntity, Long> {

    List<LogEntryJpaEntity> findAllByOrderByCreatedAtDesc();

    List<LogEntryJpaEntity> findTop5ByLogTypeOrderByCreatedAtDesc(LogType logType);
}
