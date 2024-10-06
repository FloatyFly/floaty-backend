package ch.floaty.domain;

import org.springframework.data.repository.CrudRepository;

public interface ISessionTokenRepository extends CrudRepository<SessionToken, Long> {
    SessionToken findByToken(String token);
}
