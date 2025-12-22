-- V4: Make content column nullable for INSIGHT logs
-- Created: 2025-12-23
-- Description: INSIGHT logs don't require content field

-- Make content nullable
ALTER TABLE log_entry
    ALTER COLUMN content DROP NOT NULL;

-- Add comment
COMMENT ON COLUMN log_entry.content IS 'User journal entry text (required for DAILY/SENSORY, optional for INSIGHT)';
