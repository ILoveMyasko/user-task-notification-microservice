package main.exceptions;

public class ServiceRequestFailedException extends RuntimeException {
    public ServiceRequestFailedException(String message) {
        super(message);
    }
}
