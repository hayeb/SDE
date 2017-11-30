CREATE TABLE completed_task (
	id          BIGINT PRIMARY KEY,
	description TEXT,
	date        DATE NOT NULL,
	image       BYTEA
);

ALTER TABLE assigned_task ADD COLUMN completed_task BIGINT;
ALTER TABLE assigned_task ADD CONSTRAINT fk__assigned_task__completed_task FOREIGN KEY (completed_task) REFERENCES completed_task (id);
ALTER TABLE assigned_task ADD CONSTRAINT ck__complete_status CHECK (completed_task IS NULL OR status = 'DONE');