package main.exceptions;

public class RequestedEntityNotFoundException extends RuntimeException {
    public RequestedEntityNotFoundException(String message) {
        super(message);
    }
}
