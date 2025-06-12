package ch.floaty.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackStatistics {
    private int totalPoints;
    private int durationSeconds;
    private double distanceMeters;
    private int maxAltitudeMeters;
    private int minAltitudeMeters;
    private double maxSpeedMetersPerSecond;
    private double maxClimbRateMetersPerSecond;
    private double maxSinkRateMetersPerSecond;
    private double averageSpeedMetersPerSecond;
}
