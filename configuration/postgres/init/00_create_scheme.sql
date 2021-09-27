CREATE TABLE users (
  id       BIGSERIAL   PRIMARY KEY,
  username TEXT        NOT NULL UNIQUE,
  password TEXT        NOT NULL,
  active   BOOLEAN     NOT NULL DEFAULT TRUE,
  created  timestamptz NOT NULL DEFAULT current_timestamp
);

CREATE TABLE roles (
    id   BIGSERIAL  PRIMARY KEY,
    name TEXT       NOT NULL UNIQUE
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL REFERENCES users ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE tokens (
    token     TEXT        PRIMARY KEY,
    user_id   BIGINT      NOT NULL REFERENCES users ON DELETE CASCADE,
    created   timestamptz NOT NULL DEFAULT current_timestamp,
    last_used timestamptz NOT NULL DEFAULT current_timestamp
);

CREATE TABLE password_reset_codes (
    id      BIGSERIAL   PRIMARY KEY,
    code    UUID        NOT NULL DEFAULT gen_random_uuid(),
    user_id BIGINT      NOT NULL REFERENCES users ON DELETE CASCADE,
    created timestamptz NOT NULL DEFAULT current_timestamp
);

CREATE TABLE wrong_usernames (
    id       BIGSERIAL   PRIMARY KEY,
    username TEXT        NOT NULL UNIQUE
);

CREATE TABLE wrong_passwords (
    id       BIGSERIAL   PRIMARY KEY,
    password TEXT        NOT NULL UNIQUE
);

CREATE TABLE categories (
    id   BIGSERIAL PRIMARY KEY,
    name TEXT      NOT NULL UNIQUE
);

CREATE TABLE posts (
    id          BIGSERIAL   PRIMARY KEY,
    author_id   BIGINT      NOT NULL REFERENCES users,
    category_id BIGINT      NOT NULL REFERENCES categories,
    title       TEXT        NOT NULL,
    content     TEXT        NOT NULL,
    created     timestamptz NOT NULL DEFAULT current_timestamp,
    deleted     timestamptz DEFAULT NULL
);

CREATE TABLE comments (
    id        BIGSERIAL   PRIMARY KEY,
    post_id   BIGINT      NOT NULL REFERENCES posts,
    author_id BIGINT      NOT NULL REFERENCES users,
    content   TEXT        NOT NULL,
    created   timestamptz NOT NULL DEFAULT current_timestamp,
    deleted   timestamptz DEFAULT NULL
);
