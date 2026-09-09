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

    // Manufacturer size designation, e.g. "95" (Skywalk) or "24" (Advance). These labels are
    // not comparable across manufacturers, so this is deliberately free text rather than a number.
    // Column is size_label, not size, because "size" is reserved in MySQL and quoting it would
    // differ between MySQL and the H2 used by the dev/test profiles.
    @Column(name = "size_label")
    private String size;

    // Null means "not recorded", which is a different fact from CertificationClass.NONE.
    @Enumerated(EnumType.STRING)
    @Column(name = "certification_class", length = 16)
    private CertificationClass certificationClass;

    // Only meaningful alongside a certificationClass; enforced at the API, not in the schema.
    @Enumerated(EnumType.STRING)
    @Column(name = "gradation", length = 16)
    private Gradation gradation;
}


