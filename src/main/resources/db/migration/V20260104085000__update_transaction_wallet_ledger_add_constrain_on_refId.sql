BEGIN;
ALTER TABLE transactions
ADD CONSTRAINT uq_transactions_reference_id UNIQUE (reference_id);

ALTER TABLE ledger_entry
ADD CONSTRAINT uq_ledger_reference_entry UNIQUE (reference_id);

ALTER TABLE wallets
ALTER COLUMN wallet_name SET DEFAULT 'POLYGON-AMOY';

COMMIT;