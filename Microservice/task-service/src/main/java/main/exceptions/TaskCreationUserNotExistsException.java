package main.exceptions;

public class TaskCreationUserNotExistsException extends RuntimeException {
    public TaskCreationUserNotExistsException(String message) {
        super(message);
    }
}
