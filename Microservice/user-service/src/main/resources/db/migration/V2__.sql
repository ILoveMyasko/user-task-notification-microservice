
CREATE SEQUENCE IF NOT EXISTS users_user_id_seq START WITH 1 INCREMENT BY 1;

CREATE SEQUENCE IF NOT EXISTS users_user_id_seq;
ALTER TABLE users
    ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE users
    ALTER COLUMN user_id SET DEFAULT nextval('users_user_id_seq');

ALTER SEQUENCE users_user_id_seq OWNED BY users.user_id;