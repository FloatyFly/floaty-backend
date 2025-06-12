package ch.floaty.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrackBoundingBox {
    private double minLatitude;
    private double maxLatitude;
    private double minLongitude;
    private double maxLongitude;

    public boolean isEmpty() {
        return minLatitude == 0 && maxLatitude == 0 && minLongitude == 0 && maxLongitude == 0;
    }
}
