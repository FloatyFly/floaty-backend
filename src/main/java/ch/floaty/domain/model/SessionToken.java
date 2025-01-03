package ch.floaty.domain.model;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "t_session_token")
public class SessionToken extends TimedToken {

    public static final int TTL_MINUTES = 10;

    public SessionToken(User user) {
        super(user, () -> UUID.randomUUID().toString(), TTL_MINUTES);
    }
}
