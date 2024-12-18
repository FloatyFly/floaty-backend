package ch.floaty.domain.service;

import ch.floaty.domain.model.PasswordResetToken;
import ch.floaty.domain.model.SessionToken;
import ch.floaty.domain.model.User;
import ch.floaty.domain.service.AuthenticationExceptions.EMailVerificationFailedException;
import ch.floaty.domain.service.AuthenticationExceptions.EmailAlreadyVerifiedException;
import ch.floaty.domain.service.AuthenticationExceptions.UserNotFoundException;

import java.util.Optional;

public interface IAuthenticationService {
    User register(String username, String email, String password);
    SessionToken login(String username, String password);
    void verifyEmail(String emailVerificationToken) throws EmailAlreadyVerifiedException, EMailVerificationFailedException;
    void resetPassword(String username, String password);
    void logout(Long userId) throws UserNotFoundException;
    Optional<PasswordResetToken> initiatePasswordReset(String email);
}
