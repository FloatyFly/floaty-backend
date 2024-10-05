package ch.floaty.domain;

public class AuthenticationExceptions {

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException() {
        }
    }

    public static class WrongPasswordException extends RuntimeException {
        public WrongPasswordException() {
            super();
        }
    }
}
