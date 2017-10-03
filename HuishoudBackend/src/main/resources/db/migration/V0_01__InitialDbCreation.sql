CREATE SEQUENCE hibernate_sequence;

CREATE TABLE users (
  id        BIGINT CONSTRAINT pk__users PRIMARY KEY,
  user_name VARCHAR(255) NOT NULL UNIQUE
);