BEGIN;

ALTER TABLE transactions
ADD COLUMN balance_after NUMERIC(38, 8);

COMMIT;