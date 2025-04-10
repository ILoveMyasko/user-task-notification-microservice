package main.exceptions;

public class TaskCreationErrorException extends RuntimeException {
    public TaskCreationErrorException(String message) {
        super(message);
    }
}
