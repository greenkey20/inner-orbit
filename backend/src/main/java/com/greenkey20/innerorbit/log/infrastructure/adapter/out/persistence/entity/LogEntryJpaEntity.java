package com.greenkey20.innerorbit.log.infrastructure.adapter.out.persistence.entity;

import com.greenkey20.innerorbit.log.domain.model.LogType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * LogEntry JPA 엔티티 — 영속성 레이어 전용
 * DB 스키마: log_entry (변경 없음)
 */
@Entity
@Table(name = "log_entries", indexes = {
    @Index(name = "idx_log_entries_created_at", columnList = "created_at"),
    @Index(name = "idx_log_entries_user_id", columnList = "user_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogEntryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private Integer stability;

    @Column(nullable = false)
    private Integer gravity;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Type(JsonBinaryType.class)
    @Column(name = "analysis_result", columnDefinition = "TEXT")
    private Map<String, Object> analysisResult;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 500)
    private String location;

    @Column(name = "sensory_visual", columnDefinition = "TEXT")
    private String sensoryVisual;

    @Column(name = "sensory_auditory", columnDefinition = "TEXT")
    private String sensoryAuditory;

    @Column(name = "sensory_tactile", columnDefinition = "TEXT")
    private String sensoryTactile;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type", nullable = false, length = 20)
    @Builder.Default
    private LogType logType = LogType.DAILY;

    @Column(name = "insight_abstraction", columnDefinition = "TEXT")
    private String insightAbstraction;

    @Column(name = "insight_application", columnDefinition = "TEXT")
    private String insightApplication;

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
