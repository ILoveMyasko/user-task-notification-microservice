CREATE TABLE notifications
(
    notification_id BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
    user_id         BIGINT,
    task_id         BIGINT,
    text            VARCHAR(255),
    created_at      TIMESTAMP WITH TIME ZONE,
    CONSTRAINT pk_notifications PRIMARY KEY (notification_id)
);