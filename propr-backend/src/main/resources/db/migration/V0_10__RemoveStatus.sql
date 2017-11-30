ALTER TABLE assigned_task DROP CONSTRAINT IF EXISTS ck__complete_status;

ALTER TABLE assigned_task DROP COLUMN status;