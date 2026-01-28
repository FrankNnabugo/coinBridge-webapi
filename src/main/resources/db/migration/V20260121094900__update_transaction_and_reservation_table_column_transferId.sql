BEGIN;

ALTER TABLE transactions
ALTER COLUMN transfer_id DROP NOT NULL;

ALTER TABLE transactions
RENAME COLUMN transfer_id TO provider_transaction_id;

ALTER TABLE wallet_reservations
RENAME COLUMN transaction_id TO provider_transaction_id;

COMMIT;

