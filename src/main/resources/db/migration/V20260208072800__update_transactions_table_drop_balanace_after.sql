BEGIN;
ALTER TABLE transactions
DROP COLUMN balance_after;
COMMIT;