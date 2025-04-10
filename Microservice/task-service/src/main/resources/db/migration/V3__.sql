CREATE TABLE tasks
(
    task_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id          BIGINT                      NOT NULL,
    task_title       VARCHAR(100),
    task_description VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    is_completed     BOOLEAN                     NOT NULL,
    CONSTRAINT pk_tasks PRIMARY KEY (task_id)
);