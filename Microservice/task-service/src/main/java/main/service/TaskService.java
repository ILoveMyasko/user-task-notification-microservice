package main.service;

import main.model.Task;

import java.util.List;

public interface TaskService {
    Task getTaskById(long id);

    Task createTask(Task task);

    List<Task> getAllTasks();

    Task deleteTaskById(long id);

    List<Task> getTasksByUserId(long id);
}
