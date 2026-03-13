CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

-- 초기 admin 계정 시드 (id=1) — Phase 2에서 BCrypt 해시로 교체 예정
INSERT INTO users (username, password, created_at)
VALUES ('admin', 'PENDING_DB_AUTH_PHASE2', now());

COMMENT ON TABLE users IS 'User accounts for Inner Orbit';
COMMENT ON COLUMN users.password IS 'BCrypt hashed password (placeholder until Phase 2)';
