CREATE TABLE task (
	id BIGINT CONSTRAINT pk__task__id PRIMARY KEY,
	name VARCHAR(255) NOT NULL,
	description TEXT NOT NULL,
	assignee BIGINT CONSTRAINT fk__task__assignee REFERENCES users(user_id),
	"group" BIGINT CONSTRAINT fk__task__group REFERENCES groups(id),
	due_date DATE NOT NULL
);