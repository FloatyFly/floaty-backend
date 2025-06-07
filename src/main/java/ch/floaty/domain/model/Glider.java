package ch.floaty.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_glider")
public class Glider {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "fk_user_id")
    // Gliders are not globally viewable but "belong" to a certain user.
    private User user;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "model")
    private String model;
}


