package ch.floaty.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class SessionToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String token;
    private static final int TTL_MINUTES = 10;

    public SessionToken(User user) {
        this.user = user;
        this.token = UUID.randomUUID().toString();
        this.expirationTime = LocalDateTime.now().plusMinutes(TTL_MINUTES);
    }

    @OneToOne
    @JoinColumn(name = "fk_user_id")
    private User user;

    @Column(name = "expiration_time")
    LocalDateTime expirationTime;

    public boolean isValid() {
        return expirationTime.isAfter(LocalDateTime.now());
    }
}
