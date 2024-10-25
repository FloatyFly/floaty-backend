package ch.floaty.domain.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "t_password_reset_token")
public class PasswordResetToken extends TimedToken {

    public static final int TTL_MINUTES = 5;
    private boolean used;

    public PasswordResetToken(User user) {
        super(user, () -> UUID.randomUUID().toString(), TTL_MINUTES);
        this.used = false;
    }
}
