BEGIN;

DELETE FROM accounts
WHERE id IN (
  'cash_account',
  'fees',
  'revenue',
  'settlement_account',
  'wallet_account'
);

COMMIT;