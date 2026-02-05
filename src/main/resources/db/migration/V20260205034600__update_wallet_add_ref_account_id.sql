BEGIN;

ALTER TABLE wallets
ADD COLUMN account_id VARCHAR;

UPDATE wallets
SET account_id = '0e3775f9-e978-43ab-ae12-4c9d496f2704'
WHERE id = '9d4b3947-68ca-4693-b9f5-4901d8e50bc5';

UPDATE wallets
SET account_id = '98d6cc02-8f27-4f46-8e53-791da96a8431'
WHERE id = 'e470dc4f-2d52-4c3d-9c9d-619638d325fe';

ALTER TABLE wallets
ADD CONSTRAINT fk_wallet_account
FOREIGN KEY (account_id) REFERENCES accounts(id);

ALTER TABLE wallets
ALTER COLUMN account_id SET NOT NULL;

COMMIT;