BEGIN;

ALTER TABLE wallets DROP CONSTRAINT IF EXISTS fk_wallet_user;


ALTER TABLE users
    ALTER COLUMN id TYPE VARCHAR(36)
    USING id::text;


ALTER TABLE users
    ALTER COLUMN id DROP DEFAULT;


ALTER TABLE wallets
    ALTER COLUMN id TYPE VARCHAR(36)
    USING id::text;


ALTER TABLE wallets
    ALTER COLUMN user_id TYPE VARCHAR(36)
    USING user_id::text;


ALTER TABLE wallets
    ADD CONSTRAINT fk_wallet_user
    FOREIGN KEY (user_id) REFERENCES users(id);

COMMIT;