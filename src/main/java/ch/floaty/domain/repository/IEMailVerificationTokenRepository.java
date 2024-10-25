package ch.floaty.domain.repository;

import ch.floaty.domain.model.EmailVerificationToken;
import ch.floaty.domain.model.User;
import org.springframework.data.repository.CrudRepository;

public interface IEMailVerificationTokenRepository extends CrudRepository<EmailVerificationToken, Long> {
    EmailVerificationToken findByUser(User user);
    EmailVerificationToken findByToken(String token);
}
