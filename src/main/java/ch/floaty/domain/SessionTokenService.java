package ch.floaty.domain;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class SessionTokenService {

    private ISessionTokenRepository sessionTokenRepository;

    public SessionToken validateToken(String tokenToValidate) {
        SessionToken sessionToken = sessionTokenRepository.findByToken(tokenToValidate);
        if (sessionToken != null && !isExpired(sessionToken)) {
            return sessionToken;
        }
        return null;
    }

    private boolean isExpired(SessionToken sessionToken) {
        return sessionToken.getExpirationTime().isBefore(LocalDateTime.now());
    }
}
