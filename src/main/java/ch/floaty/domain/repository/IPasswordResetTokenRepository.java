package ch.floaty.domain.repository;

import ch.floaty.domain.model.PasswordResetToken;
import ch.floaty.domain.model.SessionToken;
import ch.floaty.domain.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface IPasswordResetTokenRepository extends CrudRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
}
