package ch.floaty.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class FlightParameters {

    @Column(name = "date_time")
    private Instant dateTime;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "fk_launch_site_id")
    private Spot launchSite;

    @ManyToOne
    @JoinColumn(name = "fk_landing_site_id")
    private Spot landingSite;

    @ManyToOne
    @JoinColumn(name = "fk_glider_id")
    private Glider glider;
}
