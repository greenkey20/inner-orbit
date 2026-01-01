-- V2: Convert analysis_result column from TEXT to JSONB
-- This migration fixes the Hibernate warning about automatic type casting
-- NOTE: This migration is idempotent - it checks if conversion is needed

-- Only convert if analysis_result is NOT already JSONB
DO $$
BEGIN
    -- Check if the column is not already jsonb type
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'log_entry'
          AND column_name = 'analysis_result'
          AND data_type != 'jsonb'
    ) THEN
        -- Convert the analysis_result column to JSONB type
        ALTER TABLE log_entry
          ALTER COLUMN analysis_result TYPE jsonb
          USING CASE
            WHEN analysis_result IS NULL THEN NULL
            WHEN analysis_result = '' THEN NULL
            ELSE analysis_result::jsonb
          END;

        RAISE NOTICE 'Converted analysis_result to JSONB';
    ELSE
        RAISE NOTICE 'analysis_result is already JSONB, skipping conversion';
    END IF;
END $$;

-- Add a comment to document the column purpose
COMMENT ON COLUMN log_entry.analysis_result IS 'AI analysis result stored as JSONB format';
