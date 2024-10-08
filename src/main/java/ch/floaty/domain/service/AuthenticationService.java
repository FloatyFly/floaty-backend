package ch.floaty.domain.service;

import ch.floaty.domain.service.AuthenticationExceptions.UserNotFoundException;
import ch.floaty.domain.service.AuthenticationExceptions.WrongPasswordException;
import ch.floaty.domain.repository.ISessionTokenRepository;
import ch.floaty.domain.repository.IUserRepository;
import ch.floaty.domain.model.SessionToken;
import ch.floaty.domain.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService implements IAuthenticationService{

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    IUserRepository userRepository;
    ISessionTokenRepository sessionTokenRepository;

    public AuthenticationService(IUserRepository userRepository, ISessionTokenRepository sessionTokenRepository) {
        this.userRepository = userRepository;
        this.sessionTokenRepository = sessionTokenRepository;
    }

    @Override
    public User register(String username, String email, String password) {
        String hashedPassword = bCryptPasswordEncoder.encode(password);
        User newUser = new User(username, email, hashedPassword);
        return this.userRepository.save(newUser);
    }

    @Override
    public SessionToken login(String username, String password) throws WrongPasswordException {
        User user = this.userRepository.findByName(username);
        if (user == null) {
            throw new UserNotFoundException();
        }
        if (bCryptPasswordEncoder.matches(password, user.getHashedPassword())) {
            SessionToken sessionToken = new SessionToken(user);
            this.sessionTokenRepository.save(sessionToken);
            return sessionToken;
        } else {
            throw new WrongPasswordException();
        }
    }

    @Override
    public void logout() {

    }
}

