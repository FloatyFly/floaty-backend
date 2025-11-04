package ch.floaty.infrastructure;

import ch.floaty.domain.model.*;
import ch.floaty.generated.*;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class FlightMappingConfig {

    public static void configure(ModelMapper modelMapper) {

        modelMapper.addConverter(new AbstractConverter<Instant, OffsetDateTime>() {
            @Override
            protected OffsetDateTime convert(Instant source) {
                return source != null ? source.atOffset(ZoneOffset.UTC) : null;
            }
        });

        modelMapper.addConverter(new AbstractConverter<String, UUID>() {
            @Override
            protected UUID convert(String source) {
                return source != null ? UUID.fromString(source) : null;
            }
        });

        modelMapper.addConverter(new AbstractConverter<IgcMetadata, FlightIgcMetadataDto>() {
            @Override
            protected FlightIgcMetadataDto convert(IgcMetadata source) {
                if (source == null) {
                    return null;
                }

                FlightIgcMetadataDto dto = new FlightIgcMetadataDto();
                dto.setFileName(source.getFileName());
                dto.setFileSize(source.getFileSize());
                dto.setUploadedAt(source.getUploadedAt().atOffset(ZoneOffset.UTC));
                dto.setChecksum(source.getChecksum());

                return dto;
            }
        });

        modelMapper.addMappings(new PropertyMap<Flight, FlightDto>() {
            @Override
            protected void configure() {
                map(source.getId(), destination.getFlightId());
                map(source.getFlightParameters().getDateTime(), destination.getDateTime());
                map(source.getFlightParameters().getDuration(), destination.getDuration());
                map(source.getFlightParameters().getDescription(), destination.getDescription());
                map(source.getFlightParameters().getLaunchSite().getId(), destination.getLaunchSpotId());
                map(source.getFlightParameters().getLandingSite().getId(), destination.getLandingSpotId());
                map(source.getFlightParameters().getGlider().getId(), destination.getGliderId());

                map(source.getIgcData().getIgcMetadata(), destination.getIgcMetadata());
            }
        });

        modelMapper.addMappings(new PropertyMap<IgcData, IgcDataDto>() {
            @Override
            protected void configure() {
                map(source.getFile(), destination.getFile());
                map(source.getIgcMetadata().getFileName(), destination.getFileName());
                map(source.getIgcMetadata().getFileSize(), destination.getFileSize());
                map(source.getIgcMetadata().getUploadedAt(), destination.getUploadedAt());
                map(source.getIgcMetadata().getChecksum(), destination.getChecksum());
            }
        });

        modelMapper.addMappings(new PropertyMap<FlightTrack, FlightTrackDto>() {
            @Override
            protected void configure() {
                map(source.getFlightId(), destination.getFlightId());
                map(source.getTrackPoints(), destination.getPoints());
                map(source.getTrackStatistics(), destination.getStatistics());
                map(source.getTrackBoundingBox(), destination.getBoundingBox());
                map(source.getProcessedAt(), destination.getProcessedAt());
            }
        });

        modelMapper.addMappings(new PropertyMap<TrackPoint, TrackPointDto>() {
            @Override
            protected void configure() {
                map(source.getLatitude(), destination.getLatitude());
                map(source.getLongitude(), destination.getLongitude());
                map(source.getAltitude(), destination.getAltitude());
                map(source.getSpeed(), destination.getSpeed());
                map(source.getVerticalRate(), destination.getVerticalRate());
                map(source.getTimestamp(), destination.getTimestamp());
            }
        });

        modelMapper.addMappings(new PropertyMap<TrackStatistics, TrackStatisticsDto>() {
            @Override
            protected void configure() {
                map(source.getTotalPoints(), destination.getTotalPoints());
                map(source.getDurationSeconds(), destination.getDuration());
                // Convert meters to kilometers
                using(ctx -> ((Double) ctx.getSource()) / 1000.0)
                    .map(source.getDistanceMeters(), destination.getDistance());
                map(source.getMaxAltitudeMeters(), destination.getMaxAltitude());
                map(source.getMinAltitudeMeters(), destination.getMinAltitude());
                // Convert m/s to km/h (multiply by 3.6)
                using(ctx -> ((Double) ctx.getSource()) * 3.6)
                    .map(source.getMaxSpeedMetersPerSecond(), destination.getMaxSpeed());
                // Climb/sink rates stay in m/s (as per API spec)
                map(source.getMaxClimbRateMetersPerSecond(), destination.getMaxClimbRate());
                map(source.getMaxSinkRateMetersPerSecond(), destination.getMaxSinkRate());
                // Convert m/s to km/h (multiply by 3.6)
                using(ctx -> ((Double) ctx.getSource()) * 3.6)
                    .map(source.getAverageSpeedMetersPerSecond(), destination.getAverageSpeed());
            }
        });

        modelMapper.addConverter(new AbstractConverter<TrackBoundingBox, BoundingBoxDto>() {
            @Override
            protected BoundingBoxDto convert(TrackBoundingBox source) {
                if (source == null) {
                    return null;
                }

                BoundingBoxDto dto = new BoundingBoxDto();

                // Create northEast corner (max lat, max lon)
                BoundingBoxNorthEastDto northEast = new BoundingBoxNorthEastDto();
                northEast.setLatitude(source.getMaxLatitude());
                northEast.setLongitude(source.getMaxLongitude());
                dto.setNorthEast(northEast);

                // Create southWest corner (min lat, min lon)
                BoundingBoxSouthWestDto southWest = new BoundingBoxSouthWestDto();
                southWest.setLatitude(source.getMinLatitude());
                southWest.setLongitude(source.getMinLongitude());
                dto.setSouthWest(southWest);

                return dto;
            }
        });
    }
}