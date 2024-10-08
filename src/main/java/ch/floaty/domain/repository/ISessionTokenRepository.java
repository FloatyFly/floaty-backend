package ch.floaty.domain.repository;

import ch.floaty.domain.model.SessionToken;
import org.springframework.data.repository.CrudRepository;

public interface ISessionTokenRepository extends CrudRepository<SessionToken, Long> {
    SessionToken findByToken(String token);
}
