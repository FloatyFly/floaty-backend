package ch.floaty.domain;

import ch.floaty.domain.model.EmailVerificationToken;
import ch.floaty.domain.model.PasswordResetToken;
import ch.floaty.domain.model.SessionToken;
import ch.floaty.domain.model.User;
import ch.floaty.domain.repository.IEMailVerificationTokenRepository;
import ch.floaty.domain.repository.IPasswordResetTokenRepository;
import ch.floaty.domain.service.AuthenticationExceptions;
import ch.floaty.domain.service.IAuthenticationService;
import ch.floaty.domain.repository.ISessionTokenRepository;
import ch.floaty.domain.repository.IUserRepository;
import com.icegreen.greenmail.store.FolderException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import static com.icegreen.greenmail.util.GreenMailUtil.getBody;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ch.floaty.run.FloatyApplication.class})
@ActiveProfiles("test")  // Will use the application-test.properties
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationServiceIntegrationTest {
    @Autowired
    private IAuthenticationService authenticationService;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private ISessionTokenRepository sessionTokenRepository;
    @Autowired
    private IEMailVerificationTokenRepository emailVerificationTokenRepository;
    @Autowired
    private IPasswordResetTokenRepository passwordResetTokenRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private GreenMail greenMail;
    @Value("${spring.mail.host}")
    private String mailHost;
    @Value("${spring.mail.port}")
    private int mailPort;
    @Value("${app.base.url}")
    private String baseUrl;

    @BeforeAll
    public void setUp() {
        greenMail = new GreenMail(new ServerSetup(mailPort, mailHost, "smtp"));
        greenMail.start();
    }

    @AfterAll
    public void tearDown() {
        if (greenMail != null && greenMail.isRunning()) {
            try {
                greenMail.stop();
            } catch (IllegalStateException e) {
                System.err.println("Failed to stop GreenMail server: " + e.getMessage());
            }
        }
    }

    @AfterEach
    public void tearDownAfterEachTest() throws FolderException {
        greenMail.purgeEmailFromAllMailboxes();
    }

    @Test
    public void testRegisterHappyPath() throws AuthenticationExceptions.WrongPasswordException {
        // Arrange
        HashMap<String, String> randomUserParameters = createRandomUserParameters();
        String testUserName = randomUserParameters.get("username");
        String testEmail = randomUserParameters.get("email");
        String testRawPassword = randomUserParameters.get("password");
        assertNull(userRepository.findByName(testUserName));

        // Act
        User registeredUser = authenticationService.register(testUserName, testEmail, testRawPassword);

        // Assert
        User registeredUserFromDb = userRepository.findByName(testUserName);
        assertEquals(registeredUser, registeredUserFromDb);
        assertTrue(bCryptPasswordEncoder.matches(testRawPassword, registeredUser.getHashedPassword()));
    }

    @Test
    public void testLoginHappyPath() throws AuthenticationExceptions.WrongPasswordException {
        // Arrange
        HashMap<String, String> randomUserParameters = createRandomUserParameters();
        String username = randomUserParameters.get("username");
        String email = randomUserParameters.get("email");
        String password = randomUserParameters.get("password");
        User registeredUser = authenticationService.register(username, email, password);
        assertFalse(registeredUser.isEmailVerified());
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByUser(registeredUser);
        authenticationService.verifyEmail(emailVerificationToken.getToken());
        registeredUser = userRepository.findById(registeredUser.getId()).orElseThrow();

        // Act
        SessionToken sessionToken = authenticationService.login(username, password);

        // Assert
        SessionToken sessionTokenFromDb = sessionTokenRepository.findById(sessionToken.getId()).orElseThrow();
        assertEquals(sessionToken.getExpirationTime().truncatedTo(ChronoUnit.SECONDS),
                sessionTokenFromDb.getExpirationTime().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(sessionToken.getToken(), sessionTokenFromDb.getToken());
        assertEquals(sessionToken.getUser(), sessionTokenFromDb.getUser());
        assertEquals(sessionToken.getId(), sessionTokenFromDb.getId());
        assertEquals(registeredUser, sessionToken.getUser());
        assertFalse(sessionToken.hasExpired());
        assertTrue(sessionToken.getExpirationTime().isAfter(LocalDateTime.now()));
        assertTrue(registeredUser.isEmailVerified());
    }

    @Test
    public void testLoginWithoutVerifyingEmailFirst() throws AuthenticationExceptions.WrongPasswordException {
        // Arrange
        HashMap<String, String> randomUserParameters = createRandomUserParameters();
        String username = randomUserParameters.get("username");
        String email = randomUserParameters.get("email");
        String password = randomUserParameters.get("password");
        authenticationService.register(username, email, password);

        // Act + Assert
        assertThrows(AuthenticationExceptions.EMailNotVerifiedException.class, () -> authenticationService.login(username, "ANY_PASSWORD"));
    }

    @Test
    public void testLoginWithWrongPassword() throws AuthenticationExceptions.WrongPasswordException {
        // Arrange
        HashMap<String, String> randomUserParameters = createRandomUserParameters();
        String username = randomUserParameters.get("username");
        String email = randomUserParameters.get("email");
        String password = randomUserParameters.get("password");
        authenticationService.register(username, email, password);
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByUser(userRepository.findByName(username));
        authenticationService.verifyEmail(emailVerificationToken.getToken());

        // Act + Assert
        assertThrows(AuthenticationExceptions.WrongPasswordException.class, () -> authenticationService.login(username, "WONG_PASSWORD"));
    }

    @Test
    public void testLoginWithNonExistingUser() throws AuthenticationExceptions.WrongPasswordException {
        // Act + Assert
        assertThrows(AuthenticationExceptions.UserNotFoundException.class, () -> authenticationService.login("WRONG_USERNAME", "ANY_PASSWORD"));
    }

    @Test
    public void testVerifyEmailHappyPath() {
        // Arrange
        HashMap<String, String> randomUserParameters = createRandomUserParameters();
        String username = randomUserParameters.get("username");
        String email = randomUserParameters.get("email");
        String password = randomUserParameters.get("password");

        User registeredUser = authenticationService.register(username, email, password);
        assertFalse(registeredUser.isEmailVerified());

        String emailContent = getBody(greenMail.getReceivedMessages()[0]);
        String verificationCode = extractVerificationToken(emailContent);

        // Act
        authenticationService.verifyEmail(verificationCode);

        // Assert
        User registeredUserFromDb = userRepository.findById(registeredUser.getId()).orElseThrow();
        assertTrue(registeredUserFromDb.isEmailVerified());
        assertEquals(verificationCode, emailVerificationTokenRepository.findByUser(registeredUserFromDb).getToken());
    }


    @Test
    void testVerifyEmailWithAlreadyVerifiedEmail() {
        // Arrange
        HashMap<String, String> randomUserParameters = createRandomUserParameters();
        String username = randomUserParameters.get("username");
        String email = randomUserParameters.get("email");
        String password = randomUserParameters.get("password");
        User registeredUser = authenticationService.register(username, email, password);
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByUser(registeredUser);
        authenticationService.verifyEmail(emailVerificationToken.getToken());
        User registeredUserFromDb = userRepository.findById(registeredUser.getId()).orElseThrow();
        assertTrue(registeredUserFromDb.isEmailVerified());

        // Act + Assert
        assertThrows(AuthenticationExceptions.EmailAlreadyVerifiedException.class,
                () -> authenticationService.verifyEmail(emailVerificationToken.getToken()));
    }

    @Test
    void testVerifyEmailWithInvalidToken() {
        // Act + Assert
        assertThrows(AuthenticationExceptions.EMailVerificationFailedException.class,
                () -> authenticationService.verifyEmail("INVALID_TOKEN"));
    }

    @Test
    void testVerifyEmailWithExpiredToken() {
        // Arrange
        HashMap<String, String> randomUserParameters = createRandomUserParameters();
        String username = randomUserParameters.get("username");
        String email = randomUserParameters.get("email");
        String password = randomUserParameters.get("password");
        User registeredUser = authenticationService.register(username, email, password);
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByUser(registeredUser);
        emailVerificationToken.setExpirationTime(LocalDateTime.now().minusDays(1));
        emailVerificationTokenRepository.save(emailVerificationToken);

        // Act + Assert
        assertThrows(AuthenticationExceptions.TokenExpiredException.class,
                () -> authenticationService.verifyEmail(emailVerificationToken.getToken()));
    }

    @Test
    void testInitiatePasswordResetHappyPath() {
        // Arrange
        HashMap<String, String> randomUserParameters = createRandomUserParameters();
        String username = randomUserParameters.get("username");
        String email = randomUserParameters.get("email");
        String password = randomUserParameters.get("password");
        authenticationService.register(username, email, password);

        // Act
        PasswordResetToken passwordResetToken = authenticationService.initiatePasswordReset(email).orElseThrow();

        // Assert
        String emailContent = getBody(greenMail.getReceivedMessages()[1]);  // Mail [0] is email verification mail.
        String initiatePasswordTokenFromMail = extractInitiatePasswordToken(emailContent);
        assertTrue(passwordResetTokenRepository.findByToken(passwordResetToken.getToken()).isPresent());
        assertEquals(passwordResetToken.getToken(), initiatePasswordTokenFromMail);
    }

    @Test
    void testInitiatePasswordResetWithNonExistingEmail() {
        // Act + Assert
        Optional<PasswordResetToken> passwordResetToken = authenticationService.initiatePasswordReset("non.existing@gmail.com");
        assertTrue(passwordResetToken.isEmpty());
    }

    @Test
    void testInitiatePasswordResetWithInvalidEmail() {
        // Act + Assert
        assertThrows(AuthenticationExceptions.EmailInvalidException.class, () -> authenticationService.initiatePasswordReset("invalid.email"));
    }

    @Test
    void testResetPasswordHappyPath() {
        // Arrange
        HashMap<String, String> randomUserParameters = createRandomUserParameters();
        String username = randomUserParameters.get("username");
        String email = randomUserParameters.get("email");
        String password = randomUserParameters.get("password");
        User registeredUser = authenticationService.register(username, email, password);
        PasswordResetToken passwordResetToken = authenticationService.initiatePasswordReset(email).orElseThrow();
        String newPassword = "newPassword";

        // Act
        authenticationService.resetPassword(passwordResetToken.getToken(), newPassword);

        // Assert
        User registeredUserFromDb = userRepository.findById(registeredUser.getId()).orElseThrow();
        assertTrue(bCryptPasswordEncoder.matches(newPassword, registeredUserFromDb.getHashedPassword()));

        // TODO: There should be a test which checks that after a password reset, there are no more session tokens for the user.
    }

    @Test
    void testResetPasswordWithInvalidToken() {
        // Act + Assert
        assertThrows(AuthenticationExceptions.InvalidPasswordResetTokenException.class, () -> authenticationService.resetPassword("INVALID_TOKEN", "newPassword"));
    }

    @Test
    void testResetPasswordWithExpiredToken() {
        // Arrange
        HashMap<String, String> randomUserParameters = createRandomUserParameters();
        String username = randomUserParameters.get("username");
        String email = randomUserParameters.get("email");
        String password = randomUserParameters.get("password");
        User registeredUser = authenticationService.register(username, email, password);
        PasswordResetToken passwordResetToken = authenticationService.initiatePasswordReset(email).orElseThrow();
        passwordResetToken.setExpirationTime(LocalDateTime.now().minusDays(1));
        passwordResetTokenRepository.save(passwordResetToken);

        // Act + Assert
        assertThrows(AuthenticationExceptions.TokenExpiredException.class, () -> authenticationService.resetPassword(passwordResetToken.getToken(), "newPassword"));
    }

    @Test
    void testResetPasswordWithUsedToken() {
        // Arrange
        HashMap<String, String> randomUserParameters = createRandomUserParameters();
        String username = randomUserParameters.get("username");
        String email = randomUserParameters.get("email");
        String password = randomUserParameters.get("password");
        User registeredUser = authenticationService.register(username, email, password);
        PasswordResetToken passwordResetToken = authenticationService.initiatePasswordReset(email).orElseThrow();
        authenticationService.resetPassword(passwordResetToken.getToken(), "newPassword");

        // Act + Assert
        assertThrows(AuthenticationExceptions.InvalidPasswordResetTokenException.class, () -> authenticationService.resetPassword(passwordResetToken.getToken(), "newPassword"));
    }

    @Test
    void testLogoutHappyPath() throws AuthenticationExceptions.WrongPasswordException {
        // Arrange
        HashMap<String, String> randomUserParameters = createRandomUserParameters();
        String username = randomUserParameters.get("username");
        String email = randomUserParameters.get("email");
        String password = randomUserParameters.get("password");
        User registeredUser = authenticationService.register(username, email, password);
        EmailVerificationToken emailVerificationToken = emailVerificationTokenRepository.findByUser(registeredUser);
        authenticationService.verifyEmail(emailVerificationToken.getToken());
        SessionToken sessionToken = authenticationService.login(username, password);

        // Act
        authenticationService.logout(registeredUser.getId());

        // Assert
        assertTrue(sessionTokenRepository.findById(sessionToken.getId()).isEmpty());
    }

    @Test
    void testLogoutWithNonExistingUser() {
        // Act + Assert
        assertThrows(AuthenticationExceptions.UserNotFoundException.class, () -> authenticationService.logout(123456789L));
    }

    private HashMap<String, String> createRandomUserParameters() {
        String randomUUID = UUID.randomUUID().toString();
        String username = "Matty Testy" + randomUUID;
        String email = "mattytesty" + randomUUID + "@example.com";
        String password = "password" + randomUUID;
        return new HashMap<>() {
            {
                put("username", username);
                put("email", email);
                put("password", password);
            }
        };
    }

    private String extractVerificationToken(String emailContent) {
        // Define the prefix text to locate the verification token in the email
        String tokenPrefix = "Your email verification code: ";
        int startIndex = emailContent.indexOf(tokenPrefix) + tokenPrefix.length();

        // Extract the token until the next newline or end of the content
        int endIndex = emailContent.indexOf("\n", startIndex);
        return emailContent.substring(startIndex, endIndex != -1 ? endIndex : emailContent.length());
    }

    private String extractInitiatePasswordToken(String emailContent) {
        // Define the prefix text to locate the password reset token in the email
        String tokenPrefix = "Your password reset code: ";
        int startIndex = emailContent.indexOf(tokenPrefix) + tokenPrefix.length();

        // Extract the token until the next newline or end of the content
        int endIndex = emailContent.indexOf("\n", startIndex);
        return emailContent.substring(startIndex, endIndex != -1 ? endIndex : emailContent.length());
    }


}