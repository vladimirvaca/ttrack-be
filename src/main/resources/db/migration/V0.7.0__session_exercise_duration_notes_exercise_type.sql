-- Rename 'time' column to 'duration' in session_exercise for semantic clarity
ALTER TABLE ttrack.session_exercise
    RENAME COLUMN time TO duration;

-- Add 'notes' column for free-text observations about the exercise execution
ALTER TABLE ttrack.session_exercise
    ADD COLUMN IF NOT EXISTS notes TEXT;
