CREATE TABLE IF NOT EXISTS inbox (
    event_id VARCHAR(255) PRIMARY KEY,
    aggregate_id BIGINT NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_inbox_aggregate_id ON inbox(aggregate_id);