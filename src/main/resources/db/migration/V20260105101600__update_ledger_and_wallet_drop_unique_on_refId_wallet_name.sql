BEGIN;
ALTER TABLE ledger_entry
DROP CONSTRAINT uq_ledger_reference_entry;

ALTER TABLE wallets
DROP COLUMN wallet_name;

COMMIT;