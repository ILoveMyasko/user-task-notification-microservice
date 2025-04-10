package main.exceptions;

public class TaskAlreadyExistsException extends RuntimeException {
  public TaskAlreadyExistsException(long id) {
    super("Task with id = " + id + " already exists.");
  }
}