-- 사전 조건: log_entry.user_id 전체가 1로 업데이트되어 있어야 함
ALTER TABLE log_entry
    ALTER COLUMN user_id SET NOT NULL,
    ADD CONSTRAINT fk_log_entry_user FOREIGN KEY (user_id) REFERENCES users(id);

COMMENT ON COLUMN log_entry.user_id IS 'FK to users table';
