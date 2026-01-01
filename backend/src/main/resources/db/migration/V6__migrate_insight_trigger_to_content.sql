-- V6: Migrate Insight Log trigger to content field
-- Created: 2026-01-01
-- Description: Unify Insight mode with Daily/Sensory by using content field for observations
--              This allows Navigation Query to be applied to Insight logs consistently

-- Step 1: Migrate existing insight_trigger data to content field
-- Only update INSIGHT logs where insight_trigger has data
UPDATE log_entry
SET content = insight_trigger
WHERE log_type = 'INSIGHT'
  AND insight_trigger IS NOT NULL
  AND insight_trigger != '';

-- Step 2: Drop the insight_trigger column
ALTER TABLE log_entry
DROP COLUMN insight_trigger;

-- Add comment to document the change
COMMENT ON COLUMN log_entry.content IS 'Main log content. For DAILY: Captain''s Log, SENSORY: Travel notes, INSIGHT: Daily observations';
