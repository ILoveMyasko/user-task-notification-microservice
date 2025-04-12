package org.main.CommonEvents;

public record TaskEvent(
        TaskEventTypeEnum eventType, // CREATE, DELETE, UPDATE
        Long taskId,
        Long userId
) {}
