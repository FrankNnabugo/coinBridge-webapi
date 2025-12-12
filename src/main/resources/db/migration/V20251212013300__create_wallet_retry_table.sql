BEGIN;
CREATE TYPE retry_status AS ENUM ('PENDING', 'SUCCESS', 'FAILED');

CREATE TABLE IF NOT EXISTS wallet_retry (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    status retry_status NOT NULL,
    retry_count BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

COMMIT;