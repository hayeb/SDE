CREATE TABLE task_rating (
	id             BIGINT CONSTRAINT pk__task_rating__id PRIMARY KEY,
	completed_task BIGINT CONSTRAINT fk__task_rating__task REFERENCES completed_task (id),
	author         BIGINT CONSTRAINT fk__task_rating__author REFERENCES users (user_id),
	score          INTEGER NOT NULL,
	comment        TEXT
);

ALTER TABLE task_rating ADD CONSTRAINT uq__one_rating_per_user_per_task UNIQUE (author, completed_task);