package main.controller;

import jakarta.validation.Valid;
import main.exceptions.*;
import main.model.Task;
import main.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(){
        return ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable("id") long uId){
        return ok(taskService.getTaskById(uId));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<List<Task>> getAllTasksByUserId(@PathVariable("id") long uId){
        return ok(taskService.getTasksByUserId(uId));
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody @Valid Task Task) { //request body builds Task object through json?
        return ResponseEntity.ok(taskService.createTask(Task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Task> deleteTask(@PathVariable("id") long tId){ //okay lets return deleted
        return ok(taskService.deleteTaskById(tId));
    }


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

