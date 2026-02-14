BEGIN;
CREATE TABLE IF NOT EXISTS account_balance(
id VARCHAR PRIMARY KEY NOT NULL,
account_id VARCHAR NOT NULL,
ledger_balance NUMERIC(38, 8),
available_balance NUMERIC(38, 8),
currency_type VARCHAR,
last_processed_ledger_entry_id VARCHAR,
version BIGINT,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_DATE,
updated_at TIMESTAMP
);

ALTER TABLE account_balance
ADD CONSTRAINT fk_account_balance_accounts
FOREIGN KEY (account_id) REFERENCES accounts(id);

COMMIT;