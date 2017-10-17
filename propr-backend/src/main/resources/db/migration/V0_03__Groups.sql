CREATE TABLE groups (
  id BIGINT CONSTRAINT pk__group__id PRIMARY KEY,
  name VARCHAR(255) UNIQUE NOT NULL,
  invite_code VARCHAR(255) UNIQUE NOT NULL,
  admin BIGINT CONSTRAINT fk__group__admin REFERENCES users(user_id)
);

CREATE TABLE users_groups (
  group_id BIGINT CONSTRAINT fk__user_groups__group REFERENCES groups(id),
  user_id BIGINT CONSTRAINT fk__user_groups__user REFERENCES users(user_id)
)