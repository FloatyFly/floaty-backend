package ch.floaty.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackPoint {
    private double latitude;
    private double longitude;
    private double altitude;
    private double speed;
    private double verticalRate;
    private OffsetDateTime timestamp;
}
