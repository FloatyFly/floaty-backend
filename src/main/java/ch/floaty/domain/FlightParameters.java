package ch.floaty.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class FlightParameters {
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    @Column(name = "take_off")
    private String takeOff;
    @Column(name = "duration")
    private Long duration;
    @Column(name = "description")
    private String description;
}
