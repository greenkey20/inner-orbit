-- Inner Orbit System - Initial Schema
-- Created: 2025-12-16
-- Description: Base schema with LogEntry table including Travel Mode fields

-- Create log_entry table
CREATE TABLE log_entry (
    id BIGSERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    stability INTEGER NOT NULL,
    gravity INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    analysis_result JSONB,
    user_id BIGINT,
    location VARCHAR(500),
    sensory_visual TEXT,
    sensory_auditory TEXT,
    sensory_tactile TEXT,
    is_deep_log BOOLEAN NOT NULL DEFAULT false
);

-- Create indexes for better query performance
CREATE INDEX idx_created_at ON log_entry(created_at);
CREATE INDEX idx_user_id ON log_entry(user_id);

-- Add comments for documentation
COMMENT ON TABLE log_entry IS 'User emotional logs with Gravity/Stability metrics';
COMMENT ON COLUMN log_entry.content IS 'User journal entry text';
COMMENT ON COLUMN log_entry.stability IS 'Inner stability score (0-100)';
COMMENT ON COLUMN log_entry.gravity IS 'External pressure score (0-100)';
COMMENT ON COLUMN log_entry.analysis_result IS 'AI cognitive distortion analysis results (JSON)';
COMMENT ON COLUMN log_entry.location IS 'Travel Mode: Location or place';
COMMENT ON COLUMN log_entry.sensory_visual IS 'Travel Mode: Visual sensory details';
COMMENT ON COLUMN log_entry.sensory_auditory IS 'Travel Mode: Auditory sensory details';
COMMENT ON COLUMN log_entry.sensory_tactile IS 'Travel Mode: Tactile sensory details';
COMMENT ON COLUMN log_entry.is_deep_log IS 'Flag indicating Travel Mode entry';
