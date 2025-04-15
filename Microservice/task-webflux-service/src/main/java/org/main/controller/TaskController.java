package org.main.controller;

import jakarta.validation.Valid;
import org.main.exceptions.*;
import org.main.model.Task;
import org.main.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

//    @GetMapping
//    public ResponseEntity<List<Task>> getAllTasks(){
//        return ok(taskService.getAllTasks());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Task> getTaskById(@PathVariable("id") long uId){
//        return ok(taskService.getTaskById(uId));
//    }
//
//    @GetMapping("/users/{id}")
//    public ResponseEntity<List<Task>> getAllTasksByUserId(@PathVariable("id") long uId){
//        return ok(taskService.getTasksByUserId(uId));
//    }

    @PostMapping
    public Mono<ResponseEntity<Task>> createTask(@RequestBody @Valid Task task) {
        return taskService.createTask(task)
                .map(savedTask -> ResponseEntity
                        .status(HttpStatus.CREATED) // Use 201 CREATED for successful creation
                        .body(savedTask)) // Inside map, savedTask is the actual Task object
                .onErrorResume(TaskAlreadyExistsException.class, // Add error handling!
                        e -> Mono.just(ResponseEntity.status(HttpStatus.CONFLICT).build())) // Example error handling
                .onErrorResume(TaskCreationUserNotExistsException.class,
                        e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).build())) // Example error handling
                // Add other necessary error handling (e.g., validation, service unavailable)
                .onErrorResume(Exception.class, // Fallback
                        e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build()));
    }

//    @DeleteMapping("/{id}")
//    public Mono<ResponseEntity<Task>> deleteTask(@PathVariable("id") long tId){ //okay lets return deleted
//        return Mono.just(ok(taskService.deleteTaskById(tId)));
//    }

    //TODO return Mono?
    @ExceptionHandler(TaskAlreadyExistsException.class)
    public ResponseEntity<String> handleTaskAlreadyExistsException(TaskAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    @ExceptionHandler(TaskNotFoundException.class)
    public ResponseEntity<String> handleTaskNotFoundException(TaskNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
    @ExceptionHandler(TaskCreationUserNotExistsException.class)
    public ResponseEntity<String> handleTaskCreationErrorException(TaskCreationUserNotExistsException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());// a stub
    }
    @ExceptionHandler(TaskCreationUserServiceUnavailableException.class)
    public ResponseEntity<String> handleTaskCreationUserServiceUnavailableException(TaskCreationUserServiceUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(e.getMessage());// a stub
    }
    @ExceptionHandler(TaskSerializationException.class)
    public ResponseEntity<String> handleTaskSerializationException(TaskSerializationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());// a stub
    }
}

