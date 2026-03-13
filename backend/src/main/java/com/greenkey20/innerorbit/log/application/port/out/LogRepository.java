package com.greenkey20.innerorbit.log.application.port.out;

import com.greenkey20.innerorbit.log.domain.model.LogEntry;
import com.greenkey20.innerorbit.log.domain.model.LogType;

import java.util.List;
import java.util.Optional;

/**
 * 로그 영속성 out port (도메인 언어)
 */
public interface LogRepository {

    LogEntry save(LogEntry logEntry);

    Optional<LogEntry> findById(Long id);

    List<LogEntry> findAllOrderByCreatedAtDesc();

    List<LogEntry> findTop5ByLogType(LogType logType);

    List<LogEntry> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    List<LogEntry> findTop5ByLogTypeAndUserId(LogType logType, Long userId);

    void flush();
}
