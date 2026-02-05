BEGIN;
ALTER TABLE ledger_entry
ALTER COLUMN direction DROP NOT NULL;

ALTER TABLE ledger_entry
DROP COLUMN direction;

ALTER TABLE ledger_entry
ALTER COLUMN provider DROP NOT NULL;

ALTER TABLE ledger_entry
DROP COLUMN provider;

ALTER TABLE ledger_entry
ALTER COLUMN reference_id DROP NOT NULL;

ALTER TABLE ledger_entry
DROP COLUMN reference_id;

ALTER TABLE ledger_entry
DROP COLUMN source_address;

ALTER TABLE ledger_entry
DROP COLUMN destination_address;

ALTER TABLE ledger_entry
ALTER COLUMN balance_before DROP NOT NULL;

ALTER TABLE ledger_entry
DROP COLUMN balance_before;

ALTER TABLE ledger_entry
ALTER COLUMN balance_after DROP NOT NULL;

ALTER TABLE ledger_entry
DROP COLUMN balance_after;

ALTER TABLE ledger_entry
DROP COLUMN source_currency;

ALTER TABLE ledger_entry
DROP COLUMN destination_currency;

ALTER TABLE ledger_entry
ALTER COLUMN wallet_id DROP NOT NULL;

ALTER TABLE ledger_entry
DROP COLUMN wallet_id;

ALTER TABLE ledger_entry
DROP COLUMN updated_at;

ALTER TABLE ledger_entry
ADD COLUMN account_id VARCHAR NOT NULL;

ALTER TABLE ledger_entry
ADD COLUMN transaction_id VARCHAR NOT NULL;


---Foreign key constraints
ALTER TABLE ledger_entry
ADD CONSTRAINT fk_ledger_accounts
FOREIGN KEY (account_id) REFERENCES accounts(id);

ALTER TABLE ledger_entry
ADD CONSTRAINT fk_ledger_transaction
FOREIGN KEY (transaction_id) REFERENCES transactions(id);
COMMIT;