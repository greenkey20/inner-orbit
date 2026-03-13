package com.greenkey20.innerorbit.weeklyreport.infrastructure.adapter.out.persistence;

import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReportContent;
import com.greenkey20.innerorbit.weeklyreport.domain.model.WeeklyReportStatus;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * WeeklyReport JPA 엔티티 — 영속성 레이어 전용
 */
@Entity
@Table(name = "weekly_reports", indexes = {
    @Index(name = "idx_weekly_reports_user_id", columnList = "user_id"),
    @Index(name = "idx_weekly_reports_week_start", columnList = "week_start")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeeklyReportJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @Column(name = "week_end", nullable = false)
    private LocalDate weekEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private WeeklyReportStatus status;

    @Column(name = "log_count", nullable = false)
    private Integer logCount;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private WeeklyReportContent report;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
