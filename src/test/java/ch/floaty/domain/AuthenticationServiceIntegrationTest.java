package ch.floaty.domain;

import ch.floaty.domain.model.SessionToken;
import ch.floaty.domain.model.User;
import ch.floaty.domain.service.AuthenticationExceptions;
import ch.floaty.domain.service.IAuthenticationService;
import ch.floaty.domain.repository.ISessionTokenRepository;
import ch.floaty.domain.repository.IUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = {ch.floaty.run.FloatyApplication.class})
public class AuthenticationServiceIntegrationTest {

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private ISessionTokenRepository sessionTokenRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

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

        // Act
        SessionToken sessionToken = authenticationService.login(username, password);

        // Assert
        SessionToken sessionTokenFromDb = sessionTokenRepository.findById(sessionToken.getId()).orElseThrow();
        assertEquals(sessionToken, sessionTokenFromDb);
        assertEquals(registeredUser, sessionToken.getUser());
        assertTrue(sessionToken.isValid());
        assertTrue(sessionToken.getExpirationTime().isAfter(LocalDateTime.now()));
    }

    @Test
    public void testLoginWithWrongPassword() throws AuthenticationExceptions.WrongPasswordException {
        // Arrange
        HashMap<String, String> randomUserParameters = createRandomUserParameters();
        String username = randomUserParameters.get("username");
        String email = randomUserParameters.get("email");
        String password = randomUserParameters.get("password");
        authenticationService.register(username, email, password);

        // Act + Assert
        assertThrows(AuthenticationExceptions.WrongPasswordException.class, () -> authenticationService.login(username, "WONG_PASSWORD"));
    }

    @Test
    public void testLoginWithNonExistingUser() throws AuthenticationExceptions.WrongPasswordException {
        // Act + Assert
        assertThrows(AuthenticationExceptions.UserNotFoundException.class, () -> authenticationService.login("WRONG_USERNAME", "ANY_PASSWORD"));
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


}