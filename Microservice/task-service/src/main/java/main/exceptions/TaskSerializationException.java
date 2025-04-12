package main.exceptions;

public class TaskSerializationException extends RuntimeException {
    public TaskSerializationException(String message) {
        super(message);
    }
}
