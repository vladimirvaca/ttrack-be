CREATE TABLE IF NOT EXISTS ttrack.training_session (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR,
    description VARCHAR,
    status VARCHAR NOT NULL DEFAULT 'STARTED',
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_training_session_user FOREIGN KEY (user_id) REFERENCES ttrack.user(id)
);
