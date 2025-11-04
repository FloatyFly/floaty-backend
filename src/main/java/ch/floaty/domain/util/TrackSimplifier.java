package ch.floaty.domain.util;

import ch.floaty.domain.model.TrackPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for simplifying GPS tracks using the Douglas-Peucker algorithm.
 *
 * The Douglas-Peucker algorithm reduces the number of points in a track while
 * preserving its overall shape. This is essential for rendering large IGC files
 * efficiently in the frontend without freezing the browser.
 */
public class TrackSimplifier {

    /**
     * Simplify a track using the Douglas-Peucker algorithm.
     *
     * @param points The original track points
     * @param epsilon The tolerance in meters. Higher values = more simplification.
     *                Recommended: 10-50 meters for paragliding tracks
     * @return Simplified list of track points
     */
    public static List<TrackPoint> simplify(List<TrackPoint> points, double epsilon) {
        if (points == null || points.size() <= 2) {
            return new ArrayList<>(points);
        }

        // Apply Douglas-Peucker algorithm
        List<TrackPoint> simplified = new ArrayList<>();
        douglasPeucker(points, 0, points.size() - 1, epsilon, simplified);

        return simplified;
    }

    /**
     * Recursive implementation of Douglas-Peucker algorithm
     */
    private static void douglasPeucker(List<TrackPoint> points, int startIndex, int endIndex,
                                       double epsilon, List<TrackPoint> result) {
        // Always include the start point
        if (result.isEmpty()) {
            result.add(points.get(startIndex));
        }

        // Find the point with maximum perpendicular distance from the line
        double maxDistance = 0.0;
        int maxIndex = startIndex;

        TrackPoint start = points.get(startIndex);
        TrackPoint end = points.get(endIndex);

        for (int i = startIndex + 1; i < endIndex; i++) {
            double distance = perpendicularDistance(points.get(i), start, end);
            if (distance > maxDistance) {
                maxDistance = distance;
                maxIndex = i;
            }
        }

        // If max distance is greater than epsilon, recursively simplify
        if (maxDistance > epsilon) {
            // Recursively simplify both segments
            douglasPeucker(points, startIndex, maxIndex, epsilon, result);
            douglasPeucker(points, maxIndex, endIndex, epsilon, result);
        } else {
            // Add the end point
            result.add(points.get(endIndex));
        }
    }

    /**
     * Calculate perpendicular distance from a point to a line segment.
     * Uses the cross-track distance formula for geographic coordinates.
     *
     * @param point The point to measure distance from
     * @param lineStart Start of the line segment
     * @param lineEnd End of the line segment
     * @return Distance in meters
     */
    private static double perpendicularDistance(TrackPoint point, TrackPoint lineStart, TrackPoint lineEnd) {
        final double EARTH_RADIUS = 6371000.0; // meters

        // Convert to radians
        double lat1 = Math.toRadians(lineStart.getLatitude());
        double lon1 = Math.toRadians(lineStart.getLongitude());
        double lat2 = Math.toRadians(lineEnd.getLatitude());
        double lon2 = Math.toRadians(lineEnd.getLongitude());
        double lat3 = Math.toRadians(point.getLatitude());
        double lon3 = Math.toRadians(point.getLongitude());

        // Angular distance from start to end
        double d13 = 2 * Math.asin(Math.sqrt(
            Math.pow(Math.sin((lat1 - lat3) / 2), 2) +
            Math.cos(lat1) * Math.cos(lat3) * Math.pow(Math.sin((lon1 - lon3) / 2), 2)
        ));

        // If the line segment is very short, just use the distance to lineStart
        double d12 = 2 * Math.asin(Math.sqrt(
            Math.pow(Math.sin((lat1 - lat2) / 2), 2) +
            Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin((lon1 - lon2) / 2), 2)
        ));

        if (d12 < 1e-10) {
            return d13 * EARTH_RADIUS;
        }

        // Bearing from start to end
        double bearing12 = Math.atan2(
            Math.sin(lon2 - lon1) * Math.cos(lat2),
            Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1)
        );

        // Bearing from start to point
        double bearing13 = Math.atan2(
            Math.sin(lon3 - lon1) * Math.cos(lat3),
            Math.cos(lat1) * Math.sin(lat3) - Math.sin(lat1) * Math.cos(lat3) * Math.cos(lon3 - lon1)
        );

        // Cross-track distance (perpendicular distance)
        double crossTrackDistance = Math.abs(Math.asin(Math.sin(d13) * Math.sin(bearing13 - bearing12)));

        return crossTrackDistance * EARTH_RADIUS;
    }

    /**
     * Get recommended epsilon value based on track length and point count.
     * Uses very conservative values to preserve paragliding-specific features like thermals.
     *
     * Thermals typically have a diameter of 50-200m, so we need to preserve
     * details at that scale to keep the spiral pattern visible. These values
     * prioritize visual accuracy over aggressive compression.
     *
     * @param totalPoints Number of points in the original track
     * @return Recommended epsilon in meters
     */
    public static double getRecommendedEpsilon(int totalPoints) {
        if (totalPoints < 3000) {
            return 0.0; // No simplification needed for short flights (< 45 min)
        } else if (totalPoints < 7000) {
            return 2.0; // Minimal simplification (45 min - 2 hours)
        } else if (totalPoints < 12000) {
            return 3.0; // Very light simplification (2-3.5 hours)
        } else if (totalPoints < 18000) {
            return 4.5; // Light simplification (3.5-5 hours)
        } else {
            return 6.0; // Still very conservative for extra long flights (5+ hours)
        }
    }

    /**
     * Calculate the reduction percentage after simplification.
     *
     * @param originalSize Original number of points
     * @param simplifiedSize Simplified number of points
     * @return Reduction percentage (0-100)
     */
    public static double getReductionPercentage(int originalSize, int simplifiedSize) {
        if (originalSize == 0) return 0.0;
        return ((originalSize - simplifiedSize) * 100.0) / originalSize;
    }
}
