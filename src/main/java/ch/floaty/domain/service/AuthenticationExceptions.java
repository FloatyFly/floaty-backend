package ch.floaty.domain.service;

public class AuthenticationExceptions {

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException() {
        }
    }

    public static class WrongPasswordException extends RuntimeException {
        public WrongPasswordException() {
            super("Authentication failed.");
        }
    }

    public static class EmailAlreadyVerifiedException extends RuntimeException {
        public EmailAlreadyVerifiedException() {
            super("The provided Email is already verified.");
        }
    }

    public static class EMailVerificationFailedException extends RuntimeException {
        public EMailVerificationFailedException() {
            super("Email verification failed.");
        }
    }

    public static class EMailAlreadyUsedException extends RuntimeException {
        public EMailAlreadyUsedException() {
            super("A user with this email address already exists.");
        }
    }

    public static class EMailNotVerifiedException extends RuntimeException {
        public EMailNotVerifiedException() {
            super("Email for user is not verified yet.");
        }
    }

    public static class EmailInvalidException extends RuntimeException {
        public EmailInvalidException() {
            super("The provided Email is invalid.");
        }
    }

    public static class InsecurePasswordException extends RuntimeException {
        public InsecurePasswordException() {
            super("The provided password is insecure.");
        }
    }

    public static class UserAlreadyExistsException extends RuntimeException {
        public UserAlreadyExistsException() {
            super("The provided username is already taken.");
        }
    }

    public static class InvalidPasswordResetTokenException extends RuntimeException {
        public InvalidPasswordResetTokenException(String reason) {
            super("The provided password reset token is invalid." + " " + reason);
        }
    }

    public static class TokenExpiredException extends RuntimeException {
        public TokenExpiredException() {
            super("The provided token has expired.");
        }
    }
}
