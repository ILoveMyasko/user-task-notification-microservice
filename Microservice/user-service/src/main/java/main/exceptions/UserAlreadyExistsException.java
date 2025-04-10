package main.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(long id) {
        super(("User with id = " +  id + " already exists"));
    }
}