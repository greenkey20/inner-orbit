package com.greenkey20.innerorbit.weeklyreport.application.port.out;

import com.greenkey20.innerorbit.log.domain.model.LogEntry;

import java.time.LocalDateTime;
import java.util.List;

/**
 * weeklyreport 도메인이 log 도메인 데이터를 조회하는 out port
 * LogEntry는 log.domain.model의 순수 도메인 모델이므로 cross-domain import 허용
 */
public interface LogQueryPort {

    List<LogEntry> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime from, LocalDateTime to);
}
