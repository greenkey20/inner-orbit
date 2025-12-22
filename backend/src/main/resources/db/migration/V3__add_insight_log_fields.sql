-- V3: Add Insight Log fields to support Architecture of Insight mode
-- Created: 2025-12-22
-- Description: Add logType enum and Insight-related fields (trigger, abstraction, application, AI feedback)

-- Add log_type column to distinguish between DAILY, SENSORY, and INSIGHT logs
ALTER TABLE log_entry
    ADD COLUMN log_type VARCHAR(20) NOT NULL DEFAULT 'DAILY';

-- Add Insight Log specific fields
ALTER TABLE log_entry
    ADD COLUMN insight_trigger TEXT,
    ADD COLUMN insight_abstraction TEXT,
    ADD COLUMN insight_application TEXT,
    ADD COLUMN ai_feedback TEXT;

-- Add comments for documentation
COMMENT ON COLUMN log_entry.log_type IS 'Type of log entry: DAILY (default), SENSORY (travel mode), INSIGHT (CS concept mapping)';
COMMENT ON COLUMN log_entry.insight_trigger IS 'Insight Mode: Daily observation or event that triggered the insight';
COMMENT ON COLUMN log_entry.insight_abstraction IS 'Insight Mode: Computer Science concept abstraction';
COMMENT ON COLUMN log_entry.insight_application IS 'Insight Mode: How the CS concept applies to the observation';
COMMENT ON COLUMN log_entry.ai_feedback IS 'AI-generated feedback or review for this log entry';

-- Create index on log_type for efficient filtering
CREATE INDEX idx_log_type ON log_entry(log_type);
