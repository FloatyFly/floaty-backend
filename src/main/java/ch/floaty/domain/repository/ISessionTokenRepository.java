package ch.floaty.domain.repository;

import ch.floaty.domain.model.SessionToken;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ISessionTokenRepository extends CrudRepository<SessionToken, Long> {
    SessionToken findByToken(String token);
    List<SessionToken>findByUserId(Long userId);
}
