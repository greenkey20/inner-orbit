package com.greenkey20.innerorbit.weeklyreport.application.port.out;

import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReport;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 주간 리포트 영속성 out port
 */
public interface WeeklyReportRepository {

    WeeklyReport save(WeeklyReport weeklyReport);

    List<WeeklyReport> findAllByUserIdOrderByWeekStartDesc(Long userId);

    Optional<WeeklyReport> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndWeekStart(Long userId, LocalDate weekStart);

    Optional<WeeklyReport> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);
}
