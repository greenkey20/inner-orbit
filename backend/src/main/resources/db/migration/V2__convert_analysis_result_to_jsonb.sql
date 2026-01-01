-- V2: Convert analysis_result column from TEXT to JSONB
-- This migration fixes the Hibernate warning about automatic type casting

-- Convert the analysis_result column to JSONB type
-- USING clause tells PostgreSQL how to convert existing TEXT data to JSONB
ALTER TABLE log_entry
  ALTER COLUMN analysis_result TYPE jsonb
  USING CASE
    WHEN analysis_result IS NULL THEN NULL
    WHEN analysis_result = '' THEN NULL
    ELSE analysis_result::jsonb
  END;

-- Add a comment to document the column purpose
COMMENT ON COLUMN log_entry.analysis_result IS 'AI analysis result stored as JSONB format';
