BEGIN;

INSERT INTO accounts(id, currency, account_type) VALUES
('wallet_account', 'USDC', 'USER_WALLET'),
('settlement_account', 'USDC', 'SETTLEMENT'),
('revenue', 'USDC', 'REVENUE'),
('cash_account', 'USDC', 'CASH_ACCOUNT'),
('fees', 'USDC', 'FEES');

COMMIT;