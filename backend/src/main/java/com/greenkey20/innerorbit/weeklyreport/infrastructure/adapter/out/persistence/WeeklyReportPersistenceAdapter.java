package com.greenkey20.innerorbit.weeklyreport.infrastructure.adapter.out.persistence;

import com.greenkey20.innerorbit.weeklyreport.application.port.out.WeeklyReportRepository;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * WeeklyReportRepository out port 구현체 — JPA 영속성 어댑터
 */
@Repository
@RequiredArgsConstructor
public class WeeklyReportPersistenceAdapter implements WeeklyReportRepository {

    private final WeeklyReportJpaRepository jpaRepository;

    @Override
    public WeeklyReport save(WeeklyReport weeklyReport) {
        WeeklyReportJpaEntity entity = toJpaEntity(weeklyReport);
        WeeklyReportJpaEntity saved = jpaRepository.save(entity);
        return toDomainModel(saved);
    }

    @Override
    public List<WeeklyReport> findAllByUserIdOrderByWeekStartDesc(Long userId) {
        return jpaRepository.findAllByUserIdOrderByWeekStartDesc(userId)
                .stream().map(this::toDomainModel).toList();
    }

    @Override
    public Optional<WeeklyReport> findByIdAndUserId(Long id, Long userId) {
        return jpaRepository.findByIdAndUserId(id, userId).map(this::toDomainModel);
    }

    @Override
    public boolean existsByUserIdAndWeekStart(Long userId, LocalDate weekStart) {
        return jpaRepository.existsByUserIdAndWeekStart(userId, weekStart);
    }

    @Override
    public Optional<WeeklyReport> findByUserIdAndWeekStart(Long userId, LocalDate weekStart) {
        return jpaRepository.findByUserIdAndWeekStart(userId, weekStart).map(this::toDomainModel);
    }

    private WeeklyReportJpaEntity toJpaEntity(WeeklyReport model) {
        return WeeklyReportJpaEntity.builder()
                .id(model.getId())
                .userId(model.getUserId())
                .weekStart(model.getWeekStart())
                .weekEnd(model.getWeekEnd())
                .status(model.getStatus())
                .logCount(model.getLogCount())
                .report(model.getReport())
                .build();
    }

    private WeeklyReport toDomainModel(WeeklyReportJpaEntity entity) {
        return WeeklyReport.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .weekStart(entity.getWeekStart())
                .weekEnd(entity.getWeekEnd())
                .status(entity.getStatus())
                .logCount(entity.getLogCount())
                .report(entity.getReport())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
