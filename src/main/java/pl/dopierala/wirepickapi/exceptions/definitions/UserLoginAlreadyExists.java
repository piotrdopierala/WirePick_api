package pl.dopierala.wirepickapi.exceptions.definitions;

public class UserLoginAlreadyExists extends RuntimeException {
    public UserLoginAlreadyExists(String message) {
        super(message);
    }
}
