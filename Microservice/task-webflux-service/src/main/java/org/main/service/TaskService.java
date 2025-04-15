package org.main.service;



import org.main.model.Task;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface TaskService {
    Mono<Task> createTask(Task task);

//    Mono<Task> getTaskById(long id);
//
//    Flux<List<Task>> getAllTasks();
//
//    Mono<Task> deleteTaskById(long id);
//
//    Flux<List<Task>> getTasksByUserId(long id);
}
