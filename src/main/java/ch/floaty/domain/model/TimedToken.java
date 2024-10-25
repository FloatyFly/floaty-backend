package ch.floaty.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.function.Supplier;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class TimedToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String token;

    public TimedToken(User user, Supplier<String> tokenGenerator, int ttlMinutes) {
        this.user = user;
        this.token = tokenGenerator.get();
        this.expirationTime = LocalDateTime.now().plusMinutes(ttlMinutes);
    }

    @OneToOne
    @JoinColumn(name = "fk_user_id")
    private User user;

    @Column(name = "expiration_time")
    LocalDateTime expirationTime;

    public boolean hasExpired() {
        return expirationTime.isBefore(LocalDateTime.now());
    }
}
