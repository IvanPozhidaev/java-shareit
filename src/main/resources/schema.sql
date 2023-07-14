DROP TABLE IF EXISTS users, requests, items, comments, bookings;

CREATE TABLE IF NOT EXISTS users
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name    VARCHAR(255)                            NOT NULL,
    email   VARCHAR(512) UNIQUE                     NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS requests
(
    id  BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR(512)                            NOT NULL,
    requestor   BIGINT REFERENCES users (id)       NOT NULL,
    created     TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS items
(
    id     BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name        VARCHAR(255)                            NOT NULL,
    description VARCHAR(512)                            NOT NULL,
    available   BOOLEAN                                 NOT NULL,
    owner       BIGINT REFERENCES users (id)       NOT NULL,
    request_id  BIGINT REFERENCES requests (id) NULL,
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS bookings
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    end_time   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    item_id    BIGINT REFERENCES items (id)       NOT NULL,
    booker_id  BIGINT REFERENCES users (id)       NOT NULL,
    status     VARCHAR(64)                             NOT NULL,
    PRIMARY KEY (id)
    );

CREATE TABLE IF NOT EXISTS comments
(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text       VARCHAR(512)                            NOT NULL,
    item_id    BIGINT REFERENCES items (id)       NOT NULL,
    author_id  BIGINT REFERENCES users (id)       NOT NULL,
    created    TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
    PRIMARY KEY (id)
    );