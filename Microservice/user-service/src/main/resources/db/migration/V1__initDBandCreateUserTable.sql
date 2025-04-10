
CREATE TABLE users
(
    user_id BIGINT      NOT NULL,
    name    VARCHAR(50) NOT NULL,
    email   VARCHAR(50) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (user_id)
);