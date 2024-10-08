package ch.floaty.domain.service;

import ch.floaty.domain.repository.ISessionTokenRepository;
import ch.floaty.domain.model.SessionToken;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class SessionTokenService {

    private ISessionTokenRepository sessionTokenRepository;

    public SessionToken validateToken(String tokenToValidate) {
        SessionToken sessionToken = sessionTokenRepository.findByToken(tokenToValidate);
        if (sessionToken == null || isExpired(sessionToken)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid session token.");
        }
        return sessionToken;
    }

    public SessionToken renewToken(SessionToken sessionToken) {
        sessionToken.setExpirationTime(LocalDateTime.now().plusMinutes(10));
        return sessionTokenRepository.save(sessionToken);
    }

    private boolean isExpired(SessionToken sessionToken) {
        return sessionToken.getExpirationTime().isBefore(LocalDateTime.now());
    }

}
