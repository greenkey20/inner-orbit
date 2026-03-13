package com.greenkey20.innerorbit.weeklyreport.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * WeeklyReport Spring Data JPA 레포지토리
 */
@Repository
public interface WeeklyReportJpaRepository extends JpaRepository<WeeklyReportJpaEntity, Long> {

    List<WeeklyReportJpaEntity> findAllByUserIdOrderByWeekStartDesc(Long userId);

    Optional<WeeklyReportJpaEntity> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndWeekStart(Long userId, LocalDate weekStart);

    Optional<WeeklyReportJpaEntity> findByUserIdAndWeekStart(Long userId, LocalDate weekStart);
}
