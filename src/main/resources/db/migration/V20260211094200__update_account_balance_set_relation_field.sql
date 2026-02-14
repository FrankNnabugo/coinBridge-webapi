BEGIN;

UPDATE account_balance
SET account_id = '0e3775f9-e978-43ab-ae12-4c9d496f2704'
WHERE id = '34ec084d-9c2d-498f-b361-ffdaf205c458';

UPDATE account_balance
SET account_id = '98d6cc02-8f27-4f46-8e53-791da96a8431'
WHERE id = 'cafa8334-14ee-400c-8bf6-2c0bc905b285';

ALTER TABLE account_balance
ALTER COLUMN account_id SET NOT NULL;
COMMIT;