package ch.floaty.domain.service;

import ch.floaty.domain.model.EmailVerificationToken;
import ch.floaty.domain.model.PasswordResetToken;
import ch.floaty.domain.repository.IEMailVerificationTokenRepository;
import ch.floaty.domain.repository.IPasswordResetTokenRepository;
import ch.floaty.domain.service.AuthenticationExceptions.*;
import ch.floaty.domain.repository.ISessionTokenRepository;
import ch.floaty.domain.repository.IUserRepository;
import ch.floaty.domain.model.SessionToken;
import ch.floaty.domain.model.User;
import ch.floaty.infrastructure.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@Transactional
@Slf4j
public class AuthenticationService implements IAuthenticationService{

    @Value("${app.base.url}")
    public static String baseUrl;
    private static final String EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    IUserRepository userRepository;
    ISessionTokenRepository sessionTokenRepository;
    IEMailVerificationTokenRepository emailVerificationTokenRepository;
    IPasswordResetTokenRepository passwordResetTokenRepository;
    EmailService emailService;

    public AuthenticationService(IUserRepository userRepository,
                                 ISessionTokenRepository sessionTokenRepository,
                                 IEMailVerificationTokenRepository emailVerificationTokenRepository,
                                 IPasswordResetTokenRepository passwordResetTokenRepository,
                                 EmailService emailService) {
        this.userRepository = userRepository;
        this.sessionTokenRepository = sessionTokenRepository;
        this.emailVerificationTokenRepository = emailVerificationTokenRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.emailService = emailService;
    }

    @Override
    public User register(String username, String email, String password) {
        if (userRepository.existsByName(username)) {
            throw new UserAlreadyExistsException();
        }
        validateEmailNotTaken(email);
        validateEmailForm(email);
        validatePasswordStrength(password);

        String hashedPassword = bCryptPasswordEncoder.encode(password);
        User newUser = new User(username, email, hashedPassword);
        User storedUser = this.userRepository.save(newUser);
        EmailVerificationToken emailVerificationToken = new EmailVerificationToken(storedUser);
        this.emailVerificationTokenRepository.save(emailVerificationToken);
        String eMailText = "Your email verification code: " + emailVerificationToken.getToken();
        String eMailSubject = "Floaty Email Verification";
        emailService.sendSimpleEmail(storedUser.getEmail(), eMailSubject, eMailText);
        return storedUser;
    }

    private void validateEmailNotTaken(String email) throws EMailAlreadyUsedException {
        if (this.userRepository.existsByEmail(email)) {
            throw new EMailAlreadyUsedException();
        }
    }

    @Override
    public SessionToken login(String username, String password) throws WrongPasswordException {

        User user = this.userRepository.findByName(username);
        if (user == null) {
            throw new UserNotFoundException();
        }
        validateEmailIsVerified(user);
        if (bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
            SessionToken sessionToken = new SessionToken(user);
            this.sessionTokenRepository.save(sessionToken);
            return sessionToken;
        } else {
            throw new WrongPasswordException();
        }
    }

    private void validateEmailIsVerified(User user) throws EMailNotVerifiedException {
        if (!user.isEmailVerified()) {
            throw new EMailNotVerifiedException();
        }
    }

    @Override
    public void verifyEmail(String inputEmailVerificationToken) throws EmailAlreadyVerifiedException, EMailVerificationFailedException {
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByToken(inputEmailVerificationToken);
        if (emailVerificationToken == null) {
            throw new EMailVerificationFailedException();
        }
        if (emailVerificationToken.hasExpired()) {
            throw new TokenExpiredException();
        }
        User user = emailVerificationToken.getUser();
        if (user.isEmailVerified()) {
            throw new EmailAlreadyVerifiedException();
        } else {
            user.setEmailVerified(true);
            userRepository.save(user);
        }
        log.info("Verified email '{}' for user '{}'", user.getEmail(), user.getUsername());
    }

    @Override
    public Optional<PasswordResetToken> initiatePasswordReset(String email) {
        validateEmailForm(email);
        User user = userRepository.findByEmail(email);
        if (user != null) {
            PasswordResetToken passwordResetToken = new PasswordResetToken(user);
            passwordResetTokenRepository.save(passwordResetToken);
            String eMailText = "Your password reset code: " + passwordResetToken.getToken();
            String eMailSubject = "Floaty Password Reset";
            emailService.sendSimpleEmail(user.getEmail(), eMailSubject, eMailText);
            log.info("Initiated password reset for user '{}' and sent mail with reset token to '{}'", user.getUsername(), user.getEmail());
            return Optional.of(passwordResetToken);
        }
        return Optional.empty();
    }

    @Override
    public void resetPassword(String inputPasswordResetToken, String newPassword) throws UserNotFoundException {
        PasswordResetToken passwordResetToken = this.passwordResetTokenRepository.findByToken(inputPasswordResetToken)
                .orElseThrow(() -> new InvalidPasswordResetTokenException("Token not found."));
        User user = passwordResetToken.getUser();
        if (passwordResetToken.hasExpired()) {
            log.info("Password reset for user '{}': token expired.", user.getUsername());
            throw new TokenExpiredException();
        }
        if (passwordResetToken.isUsed()) {
            log.info("Password reset for user '{}': token already used.", user.getUsername());
            throw new InvalidPasswordResetTokenException("Token already used.");
        }
        validatePasswordStrength(newPassword);

        passwordResetToken.setUsed(true);
        sessionTokenRepository.findByUserId(user.getId()).forEach(sessionTokenRepository::delete);
        passwordResetTokenRepository.save(passwordResetToken);
        user.setHashedPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Did reset password for user '{}'", user.getUsername());
    }

    @Override
    public void logout(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        sessionTokenRepository.findByUserId(user.getId()).forEach(sessionTokenRepository::delete);
        log.info("Logged out {}. Invalidated all session tokens.", user.getUsername());
    }

    private void validatePasswordStrength(String password) throws InsecurePasswordException {
        // TODO: Add proper strong validation.
        if (password == null || password.length() < 8) {
            throw new InsecurePasswordException();
        }

    }

    private void validateEmailForm(String email) throws EmailInvalidException {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new EmailInvalidException();
        }
    }
}

