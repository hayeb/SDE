CREATE SEQUENCE hibernate_sequence;

CREATE TABLE users (
  user_id  BIGINT PRIMARY KEY,
  username VARCHAR(128) UNIQUE,
  password VARCHAR(256),
  enabled  BOOL,
  email    VARCHAR(256)
);