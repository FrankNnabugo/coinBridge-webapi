BEGIN;

CREATE TABLE wallet_reservations(
id VARCHAR(36) NOT NULL PRIMARY KEY,
wallet_id VARCHAR NOT NULL,
amount NUMERIC(38, 8) NOT NULL,
reservation_type VARCHAR NOT NULL,
status VARCHAR NOT NULL DEFAULT 'ACTIVE',
transaction_id VARCHAR,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP

);

ALTER TABLE wallet_reservations
ADD CONSTRAINT fk_reservation_wallet
FOREIGN KEY (wallet_id) REFERENCES wallets(id);

COMMIT;