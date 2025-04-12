package ch.floaty.infrastructure;

import ch.floaty.domain.service.AuthenticationExceptions;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatusException(ResponseStatusException ex) {
        return new ResponseEntity<>(ex.getReason(), ex.getStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        log.error("Unexpected error: " + ex.getMessage(), ex);
        return new ResponseEntity<>("An unexpected error occurred. We are looking into it. Please try again later.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationExceptions.EmailInvalidException.class)
    public ResponseEntity<String> handleEmailInvalidException(AuthenticationExceptions.EmailInvalidException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationExceptions.InsecurePasswordException.class)
    public ResponseEntity<String> handleInsecurePasswordException(AuthenticationExceptions.InsecurePasswordException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationExceptions.UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(AuthenticationExceptions.UserAlreadyExistsException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationExceptions.WrongPasswordException.class)
    public ResponseEntity<String> handleWrongPasswordException(AuthenticationExceptions.WrongPasswordException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationExceptions.EmailAlreadyVerifiedException.class)
    public ResponseEntity<String> handleEmailAlreadyVerifiedException(AuthenticationExceptions.EmailAlreadyVerifiedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationExceptions.EMailVerificationFailedException.class)
    public ResponseEntity<String> handleEMailVerificationFailedException(AuthenticationExceptions.EMailVerificationFailedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationExceptions.EMailAlreadyUsedException.class)
    public ResponseEntity<String> handleEMailAlreadyUsedException(AuthenticationExceptions.EMailAlreadyUsedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationExceptions.UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(AuthenticationExceptions.UserNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationExceptions.EMailNotVerifiedException.class)
    public ResponseEntity<String> handleEMailNotVerifiedException(AuthenticationExceptions.EMailNotVerifiedException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationExceptions.InvalidPasswordResetTokenException.class)
    public ResponseEntity<String> handleInvalidPasswordResetTokenException(AuthenticationExceptions.InvalidPasswordResetTokenException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationExceptions.TokenExpiredException.class)
    public ResponseEntity<String> handleTokenExpiredException(AuthenticationExceptions.TokenExpiredException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
