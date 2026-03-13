-- V10: weekly_reports 테이블 생성
CREATE TABLE weekly_reports (
    id         BIGSERIAL   PRIMARY KEY,
    user_id    BIGINT      NOT NULL REFERENCES users(id),
    week_start DATE        NOT NULL,
    week_end   DATE        NOT NULL,
    status     VARCHAR(20) NOT NULL,   -- NA | INSUFFICIENT | GENERATED
    log_count  INTEGER     NOT NULL DEFAULT 0,
    report     JSONB,                  -- null when status != GENERATED
    created_at TIMESTAMP   NOT NULL DEFAULT now(),
    CONSTRAINT uq_user_week UNIQUE (user_id, week_start)
);

CREATE INDEX idx_weekly_reports_user_id ON weekly_reports(user_id);
CREATE INDEX idx_weekly_reports_week_start ON weekly_reports(week_start DESC);
