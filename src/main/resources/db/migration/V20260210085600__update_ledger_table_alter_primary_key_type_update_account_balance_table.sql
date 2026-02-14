BEGIN;
ALTER TABLE ledger_entry
DROP CONSTRAINT IF EXISTS ledger_entry_pkey;

ALTER TABLE ledger_entry
DROP COLUMN id;

ALTER TABLE ledger_entry
ADD COLUMN id BIGINT GENERATED ALWAYS AS IDENTITY;

ALTER TABLE ledger_entry
ADD CONSTRAINT ledger_entry_pkey PRIMARY KEY (id);

ALTER TABLE account_balance
ALTER COLUMN last_processed_ledger_entry_id TYPE BIGINT
USING last_processed_ledger_entry_id::BIGINT;

COMMIT;