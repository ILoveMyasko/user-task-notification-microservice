package main.exceptions;

public class TaskCreationUserServiceUnavailableException extends RuntimeException {
    public TaskCreationUserServiceUnavailableException(String message) {
        super(message);
    }
}
