-- ============================================================================
-- E-Wallet — Oracle Seed Data
-- Populates the lookup tables (account_types, transaction_types,
-- transaction_status), three demo ATMs and one type-3 (ATM) account per ATM.
-- Run AFTER schema.sql.
-- ============================================================================

-- 1. Account types (1 = Wallet, 2 = Card, 3 = ATM)
INSERT INTO account_types (type_name) VALUES ('Wallet');
INSERT INTO account_types (type_name) VALUES ('Card');
INSERT INTO account_types (type_name) VALUES ('ATM');

-- 2. Transaction types (1 = Deposit, 2 = Withdraw, 3 = Transfer)
INSERT INTO transaction_types (type_name) VALUES ('Deposit');
INSERT INTO transaction_types (type_name) VALUES ('Withdraw');
INSERT INTO transaction_types (type_name) VALUES ('Transfer');

-- 3. Transaction statuses (1 = Pending, 2 = Success, 3 = Failed,
--    4 = Cancelled, 5 = Expired)
INSERT INTO transaction_status (status_name) VALUES ('Pending');
INSERT INTO transaction_status (status_name) VALUES ('Success');
INSERT INTO transaction_status (status_name) VALUES ('Failed');
INSERT INTO transaction_status (status_name) VALUES ('Cancelled');
INSERT INTO transaction_status (status_name) VALUES ('Expired');

-- 4. Demo ATMs
INSERT INTO atms (atm_name, atm_location, mapX, mapY)
VALUES ('Cairo Downtown ATM', 'Downtown, Cairo', 90.0444, 31.2357);

INSERT INTO atms (atm_name, atm_location, mapX, mapY)
VALUES ('Nasr City ATM', 'Nasr City, Cairo', 30.0511, 50.3656);

INSERT INTO atms (atm_name, atm_location, mapX, mapY)
VALUES ('New Cairo ATM', 'New Cairo, Cairo', 70.0074, 90.4913);

-- 5. One type-3 (ATM) account per ATM so the ledger can move money to/from
--    the machines. The ids are resolved by name so the script does not depend
--    on identity-sequence values.
INSERT INTO accounts (account_type_id, reference_id)
SELECT (SELECT account_type_id FROM account_types WHERE type_name = 'ATM'), atm_id
FROM atms;

COMMIT;