-- Make previously NOT NULL columns nullable in session_exercise
ALTER TABLE ttrack.session_exercise
    ALTER COLUMN start_time DROP NOT NULL,
    ALTER COLUMN end_time DROP NOT NULL,
    ALTER COLUMN rest_time DROP NOT NULL,
    ALTER COLUMN status DROP NOT NULL,
    ALTER COLUMN exercise_order DROP NOT NULL,
    ALTER COLUMN exercise_id DROP NOT NULL,
    ALTER COLUMN training_session_id DROP NOT NULL,
    ALTER COLUMN created_at DROP NOT NULL;

-- Add type_of_exercise column
ALTER TABLE ttrack.session_exercise
    ADD COLUMN IF NOT EXISTS type_of_exercise VARCHAR(32);

