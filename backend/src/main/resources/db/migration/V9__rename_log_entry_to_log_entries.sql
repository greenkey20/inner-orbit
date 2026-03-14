-- V9: log_entry → log_entries 테이블명 표준화 (복수 snake_case)
-- ALTER TABLE RENAME: 데이터, 인덱스, FK, 시퀀스 전부 그대로 유지됨
ALTER TABLE log_entry RENAME TO log_entries;
ALTER INDEX idx_created_at RENAME TO idx_log_entries_created_at;
ALTER INDEX idx_user_id RENAME TO idx_log_entries_user_id;
ALTER INDEX idx_log_type RENAME TO idx_log_entries_log_type;
-- PK 인덱스(log_entry_pkey)는 PostgreSQL이 테이블 rename 시 자동 반영
