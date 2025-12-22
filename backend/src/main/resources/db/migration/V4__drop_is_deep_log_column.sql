-- V4: Remove is_deep_log column (replaced by log_type)
-- Created: 2025-12-22
-- Description: Remove redundant is_deep_log column, use log_type enum instead

-- Drop the is_deep_log column
-- Note: isDeepLog = true is now represented as log_type = 'SENSORY'
ALTER TABLE log_entry
    DROP COLUMN IF EXISTS is_deep_log;

-- Add comment
COMMENT ON COLUMN log_entry.log_type IS 'Type of log entry: DAILY (default), SENSORY (travel mode, formerly isDeepLog=true), INSIGHT (CS concept mapping)';
