package ch.floaty.domain.model;


import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Random;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "t_email_verification_token")
public class EmailVerificationToken extends TimedToken {

    private static final int TTL_MINUTES = 30;

    public EmailVerificationToken(User user) {
        super(user, () -> UUID.randomUUID().toString(), TTL_MINUTES);
    }
}
