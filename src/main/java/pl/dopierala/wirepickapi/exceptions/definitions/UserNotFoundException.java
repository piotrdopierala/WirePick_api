package pl.dopierala.wirepickapi.exceptions.definitions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
