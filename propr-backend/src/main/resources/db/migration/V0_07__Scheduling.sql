CREATE TABLE task_definition
(
	id          BIGINT CONSTRAINT pk__task_definition__id PRIMARY KEY,
	name        VARCHAR(255) NOT NULL UNIQUE,
	description TEXT,
	"group"     BIGINT CONSTRAINT fk__task_definition__group REFERENCES groups (id),
	weight      VARCHAR(6)   NOT NULL,
	period_type VARCHAR(5)   NOT NULL,
	frequency   INTEGER      NOT NULL
);

CREATE TABLE assigned_task
(
	id         BIGINT CONSTRAINT pk__assigned_task__id PRIMARY KEY,
	assignee   BIGINT CONSTRAINT fk__assigned_task__assignee REFERENCES users (user_id),
	definition BIGINT CONSTRAINT fk__assigned_task_definition REFERENCES task_definition (id),
	due_date   DATE       NOT NULL,
	status     VARCHAR(7) NOT NULL
);

INSERT INTO task_definition (id, name, description, "group", weight, period_type, frequency)
	SELECT
		id,
		name,
		description,
		"group",
		'MEDIUM',
		'WEEK',
		1
	FROM task;

INSERT INTO assigned_task (id, assignee, definition, due_date, status)
	SELECT
		nextval('hibernate_sequence'),
		task.assignee,
		task.id,
		task.due_date,
		'TODO'
	FROM task;

DROP TABLE task;



