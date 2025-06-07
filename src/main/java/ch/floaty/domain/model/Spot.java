package ch.floaty.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_spot")
public class Spot {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "fk_user_id")
    // Spots are not globally viewable but "belong" to a certain user.
    private User user;

    @Column(name = "latitude")
    private double latitude;

    @Column(name = "longitude")
    private double longitude;

    @Column(name = "altitude")
    private double altitude;

    @Column(name = "description")
    private String description;

    @Column(name = "isLaunchSite")
    private boolean isLaunchSite;

    @Column(name = "isLandingSite")
    private boolean isLandingSite;
}


