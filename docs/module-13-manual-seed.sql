SET NAMES utf8mb4;

START TRANSACTION;

SET FOREIGN_KEY_CHECKS = 0;

DELETE FROM deposit_scheme_profit_distribution;
DELETE FROM deposit_scheme_schedule;
DELETE FROM deposit_scheme_enrollment;
DELETE FROM deposit_scheme;

ALTER TABLE deposit_scheme AUTO_INCREMENT = 1;
ALTER TABLE deposit_scheme_enrollment AUTO_INCREMENT = 1;
ALTER TABLE deposit_scheme_schedule AUTO_INCREMENT = 1;
ALTER TABLE deposit_scheme_profit_distribution AUTO_INCREMENT = 1;

SET FOREIGN_KEY_CHECKS = 1;

COMMIT;

-- Module 13 note:
-- Deposit scheme schedule and profit distribution rows are generated from
-- the business flow when you create scheme + enrollment through the APIs.
-- To recreate the full current environment, use:
-- 1. this cleanup/reset SQL
-- 2. docs/module-13-postman-samples.json payloads against the live APIs
-- Current seeded environment counts after API seeding:
--   deposit_scheme = 15
--   deposit_scheme_enrollment = 15
--   deposit_scheme_schedule = 186
--   deposit_scheme_profit_distribution = 86
