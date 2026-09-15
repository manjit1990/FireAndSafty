-- TRUNCATE all tables to start with a fresh database
-- CASCADE ensures that dependent records (like work orders for a user) are also deleted

TRUNCATE TABLE work_orders CASCADE;
TRUNCATE TABLE inspections CASCADE;
TRUNCATE TABLE buildings CASCADE;
TRUNCATE TABLE customers CASCADE;
TRUNCATE TABLE users CASCADE;
