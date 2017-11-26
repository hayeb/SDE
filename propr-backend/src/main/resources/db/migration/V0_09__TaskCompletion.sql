CREATE TABLE completed_task (
	id          BIGINT PRIMARY KEY,
	description TEXT,
	date        DATE NOT NULL,
	image       bytea
);

ALTER TABLE assigned_task
	ADD COLUMN completed_task BIGINT REFERENCES completed_task (id),
	ADD CHECK (completed_task IS NULL OR status = 'DONE');