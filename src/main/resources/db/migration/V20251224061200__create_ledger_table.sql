BEGIN;

CREATE TABLE ledger_entry(
id VARCHAR(36) NOT NULL PRIMARY KEY,
wallet_id VARCHAR NOT NULL,
entry_type VARCHAR NOT NULL,
amount NUMERIC(38, 8) NOT NULL,
direction VARCHAR NOT NULL,
provider VARCHAR NOT NULL,
asset VARCHAR NOT NULL,
status VARCHAR  NOT NULL,
reference_id VARCHAR NOT NULL,
source_address VARCHAR(200),
destination_address VARCHAR(200),
source_currency VARCHAR,
destination_currency VARCHAR,
created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
updated_at TIMESTAMP

);


ALTER TABLE ledger_entry
ADD CONSTRAINT fk_ledger_entry_wallet
FOREIGN KEY (wallet_id) REFERENCES wallets(id);

COMMIT;