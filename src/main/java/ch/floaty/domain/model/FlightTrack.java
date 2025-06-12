package ch.floaty.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import static java.nio.charset.StandardCharsets.UTF_8;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class FlightTrack {
    private Long flightId;
    private List<TrackPoint> trackPoints;
    private TrackStatistics trackStatistics;
    private TrackBoundingBox trackBoundingBox;
    private OffsetDateTime processedAt;

    public FlightTrack(Long flightId, IgcData igcData) {
        this.flightId = flightId;
        this.processedAt = OffsetDateTime.now();

        try {
            String igcContent = new String(igcData.getFile(), UTF_8);
            IgcParser parser = new IgcParser();
            ParsedIgcData parsedData = parser.parseIgc(igcContent);

            this.trackPoints = parsedData.getTrackPoints();
            this.trackBoundingBox = calculateBoundingBox(trackPoints);
            this.trackStatistics = calculateStatistics(trackPoints);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse IGC data", e);
        }
    }

    private TrackBoundingBox calculateBoundingBox(List<TrackPoint> points) {
        if (points.isEmpty()) {
            return new TrackBoundingBox();
        }

        double minLat = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLon = Double.MIN_VALUE;

        for (TrackPoint point : points) {
            minLat = Math.min(minLat, point.getLatitude());
            maxLat = Math.max(maxLat, point.getLatitude());
            minLon = Math.min(minLon, point.getLongitude());
            maxLon = Math.max(maxLon, point.getLongitude());
        }

        return new TrackBoundingBox(minLat, maxLat, minLon, maxLon);
    }

    private TrackStatistics calculateStatistics(List<TrackPoint> points) {
        if (points.isEmpty()) {
            return new TrackStatistics();
        }

        int totalPoints = points.size();
        TrackPoint firstPoint = points.get(0);
        TrackPoint lastPoint = points.get(points.size() - 1);

        // Duration in seconds
        long durationSeconds = lastPoint.getTimestamp().toEpochSecond() -
                firstPoint.getTimestamp().toEpochSecond();

        // Distance calculation
        double totalDistance = 0.0;
        double maxSpeed = 0.0;
        double maxClimbRate = Double.MIN_VALUE;
        double maxSinkRate = Double.MAX_VALUE;
        double speedSum = 0.0;
        int maxAltitude = Integer.MIN_VALUE;
        int minAltitude = Integer.MAX_VALUE;

        for (int i = 0; i < points.size(); i++) {
            TrackPoint point = points.get(i);

            // Altitude stats
            int altitude = (int) point.getAltitude();
            maxAltitude = Math.max(maxAltitude, altitude);
            minAltitude = Math.min(minAltitude, altitude);

            // Speed and vertical rate stats
            maxSpeed = Math.max(maxSpeed, point.getSpeed());
            maxClimbRate = Math.max(maxClimbRate, point.getVerticalRate());
            maxSinkRate = Math.min(maxSinkRate, point.getVerticalRate());
            speedSum += point.getSpeed();

            // Distance calculation
            if (i > 0) {
                TrackPoint prevPoint = points.get(i - 1);
                totalDistance += calculateDistance(prevPoint, point);
            }
        }

        double averageSpeed = speedSum / totalPoints;

        return new TrackStatistics(
                totalPoints,
                (int) durationSeconds,
                totalDistance,
                maxAltitude,
                minAltitude,
                maxSpeed,
                maxClimbRate,
                maxSinkRate,
                averageSpeed
        );
    }

    /**
     * Calculate distance between two points using Haversine formula
     */
    private double calculateDistance(TrackPoint p1, TrackPoint p2) {
        double earthRadius = 6371000; // meters

        double lat1Rad = Math.toRadians(p1.getLatitude());
        double lat2Rad = Math.toRadians(p2.getLatitude());
        double deltaLatRad = Math.toRadians(p2.getLatitude() - p1.getLatitude());
        double deltaLonRad = Math.toRadians(p2.getLongitude() - p1.getLongitude());

        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }
}

class IgcParser {

    private static final Pattern B_RECORD_PATTERN = Pattern.compile(
            "^B(\\d{6})(\\d{7}[NS])(\\d{8}[EW])([AV])(\\d{5})(\\d{5}).*$"
    );

    private static final Pattern H_DATE_PATTERN = Pattern.compile(
            "^HFDTE(?:DATE:)?(\\d{6}).*$"
    );

    public ParsedIgcData parseIgc(String igcContent) {
        String[] lines = igcContent.split("\\r?\\n");

        List<TrackPoint> trackPoints = new ArrayList<>();
        String flightDate = null;

        // First pass: extract flight date from header
        for (String line : lines) {
            if (line.startsWith("HFDTE")) {
                Matcher matcher = H_DATE_PATTERN.matcher(line);
                if (matcher.matches()) {
                    flightDate = matcher.group(1); // DDMMYY format
                    break;
                }
            }
        }

        if (flightDate == null) {
            throw new IllegalArgumentException("No flight date found in IGC file");
        }

        // Second pass: parse B records (GPS fixes)
        List<RawTrackPoint> rawPoints = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith("B")) {
                RawTrackPoint rawPoint = parseBRecord(line, flightDate);
                if (rawPoint != null) {
                    rawPoints.add(rawPoint);
                }
            }
        }

        // Third pass: calculate derived values (speed, vertical rate)
        trackPoints = calculateDerivedValues(rawPoints);

        return new ParsedIgcData(trackPoints);
    }

    private RawTrackPoint parseBRecord(String line, String flightDate) {
        Matcher matcher = B_RECORD_PATTERN.matcher(line);

        if (!matcher.matches()) {
            return null; // Skip invalid B records
        }

        try {
            String timeStr = matcher.group(1); // HHMMSS
            String latStr = matcher.group(2);  // DDMMmmmN/S
            String lonStr = matcher.group(3);  // DDDMMmmmE/W
            String validity = matcher.group(4); // A=valid, V=invalid
            String altPressureStr = matcher.group(5); // Pressure altitude
            String altGpsStr = matcher.group(6); // GPS altitude

            // Skip invalid fixes
            if (!"A".equals(validity)) {
                return null;
            }

            // Parse time and create timestamp
            OffsetDateTime timestamp = parseTimestamp(flightDate, timeStr);

            // Parse coordinates
            double latitude = parseLatitude(latStr);
            double longitude = parseLongitude(lonStr);

            // Parse altitude (prefer GPS altitude)
            double altitude = Double.parseDouble(altGpsStr);

            return new RawTrackPoint(timestamp, latitude, longitude, altitude);

        } catch (Exception e) {
            // Skip malformed records
            return null;
        }
    }

    private OffsetDateTime parseTimestamp(String flightDate, String timeStr) {
        // flightDate is DDMMYY, timeStr is HHMMSS
        String day = flightDate.substring(0, 2);
        String month = flightDate.substring(2, 4);
        String year = "20" + flightDate.substring(4, 6); // Assume 21st century

        String hour = timeStr.substring(0, 2);
        String minute = timeStr.substring(2, 4);
        String second = timeStr.substring(4, 6);

        String dateTimeStr = year + "-" + month + "-" + day + "T" +
                hour + ":" + minute + ":" + second + "Z";

        return OffsetDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    private double parseLatitude(String latStr) {
        // Format: DDMMmmmN or DDMMmmmS
        char hemisphere = latStr.charAt(latStr.length() - 1);
        String coords = latStr.substring(0, latStr.length() - 1);

        int degrees = Integer.parseInt(coords.substring(0, 2));
        double minutes = Double.parseDouble(coords.substring(2)) / 1000.0;

        double latitude = degrees + minutes / 60.0;

        return hemisphere == 'S' ? -latitude : latitude;
    }

    private double parseLongitude(String lonStr) {
        // Format: DDDMMmmmE or DDDMMmmmW
        char hemisphere = lonStr.charAt(lonStr.length() - 1);
        String coords = lonStr.substring(0, lonStr.length() - 1);

        int degrees = Integer.parseInt(coords.substring(0, 3));
        double minutes = Double.parseDouble(coords.substring(3)) / 1000.0;

        double longitude = degrees + minutes / 60.0;

        return hemisphere == 'W' ? -longitude : longitude;
    }

    private List<TrackPoint> calculateDerivedValues(List<RawTrackPoint> rawPoints) {
        List<TrackPoint> result = new ArrayList<>();

        for (int i = 0; i < rawPoints.size(); i++) {
            RawTrackPoint current = rawPoints.get(i);

            double speed = 0.0;
            double verticalRate = 0.0;

            if (i > 0 && i < rawPoints.size() - 1) {
                // Use previous and next points for better accuracy
                RawTrackPoint prev = rawPoints.get(i - 1);
                RawTrackPoint next = rawPoints.get(i + 1);

                // Calculate speed (m/s)
                double distance = calculateDistance(
                        prev.getLatitude(), prev.getLongitude(),
                        next.getLatitude(), next.getLongitude()
                );
                long timeDiff = next.getTimestamp().toEpochSecond() -
                        prev.getTimestamp().toEpochSecond();

                if (timeDiff > 0) {
                    speed = distance / timeDiff;
                }

                // Calculate vertical rate (m/s)
                double altDiff = next.getAltitude() - prev.getAltitude();
                if (timeDiff > 0) {
                    verticalRate = altDiff / timeDiff;
                }
            }

            TrackPoint trackPoint = new TrackPoint(
                    current.getLatitude(),
                    current.getLongitude(),
                    current.getAltitude(),
                    speed,
                    verticalRate,
                    current.getTimestamp()
            );

            result.add(trackPoint);
        }

        return result;
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371000; // meters

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLatRad = Math.toRadians(lat2 - lat1);
        double deltaLonRad = Math.toRadians(lon2 - lon1);

        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }
}

/**
 * Helper classes for IGC parsing
 */
@Data
@AllArgsConstructor
class RawTrackPoint {
    private OffsetDateTime timestamp;
    private double latitude;
    private double longitude;
    private double altitude;
}

@Data
@AllArgsConstructor
class ParsedIgcData {
    private List<TrackPoint> trackPoints;
}