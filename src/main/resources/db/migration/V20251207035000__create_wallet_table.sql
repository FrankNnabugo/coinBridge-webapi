CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TYPE chain_enum AS ENUM (
    'ETHEREUM',
    'POLYGON',
    'MATIC_MUMBAI',
);

CREATE TABLE IF NOT EXISTS wallets(
 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    token VARCHAR(36) NOT NULL DEFAULT 'USDC',
    blockchain VARCHAR(36) NOT NULL,
    state VARCHAR(36),
    custody_type VARCHAR NOT NULL,
    account_type VARCHAR NOT NULL,
    address VARCHAR(255) NOT NULL,
    wallet_name VARCHAR(36) NOT NULL,
    provider VARCHAR NOT NULL DEFAULT 'circle',
    wallet_set_id VARCHAR(45) NOT NULL,
    circle_wallet_id VARCHAR(100) NOT NULL,
    reference_id VARCHAR(100),
    total_balance NUMERIC(38, 8) NOT NULL DEFAULT 0,
    reserved_balance NUMERIC(38, 8) NOT NULL DEFAULT 0,
    available_balance NUMERIC(38, 8) NOT NULL DEFAULT 0,
    status VARCHAR NOT NULL DEFAULT 'active',
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NULL
);

ALTER TABLE wallets
ADD CONSTRAINT fk_wallet_user
FOREIGN KEY (user_id) REFERENCES users(id);