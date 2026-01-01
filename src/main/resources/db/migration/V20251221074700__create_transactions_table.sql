BEGIN;

CREATE TABLE transactions (
    id VARCHAR(36) PRIMARY KEY,

    type VARCHAR NOT NULL,

    user_id VARCHAR(36) NOT NULL,

    wallet_id VARCHAR(36) NOT NULL,

    amount NUMERIC NOT NULL,

    status VARCHAR NOT NULL,

    transfer_id VARCHAR(50) NOT NULL,

    reference_id VARCHAR(50),

    source_currency VARCHAR,

    destination_currency VARCHAR,

    source_address VARCHAR,

    destination_address VARCHAR,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP
);

-- Foreign key constraints
ALTER TABLE transactions
ADD CONSTRAINT fk_transactions_user
FOREIGN KEY (user_id) REFERENCES users(id);

ALTER TABLE transactions
ADD CONSTRAINT fk_transactions_wallet
FOREIGN KEY (wallet_id) REFERENCES wallets(id);


COMMIT;