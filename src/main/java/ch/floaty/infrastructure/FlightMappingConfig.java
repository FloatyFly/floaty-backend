package ch.floaty.infrastructure;

import ch.floaty.domain.model.Flight;
import ch.floaty.generated.FlightDto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

public class FlightMappingConfig {

    public static void configure(ModelMapper modelMapper) {
        // Converter for Instant to OffsetDateTime
        modelMapper.addConverter(new AbstractConverter<Instant, OffsetDateTime>() {
            @Override
            protected OffsetDateTime convert(Instant source) {
                return source != null ? source.atOffset(ZoneOffset.UTC) : null;
            }
        });

        // Converter for String ID to UUID
        modelMapper.addConverter(new AbstractConverter<String, UUID>() {
            @Override
            protected UUID convert(String source) {
                return source != null ? UUID.fromString(source) : null;
            }
        });

        modelMapper.addMappings(new PropertyMap<Flight, FlightDto>() {
            @Override
            protected void configure() {
                // Map ID - let the converter handle the String to UUID conversion
                map(source.getId(), destination.getFlightId());

                // Map nested datetime field
                map(source.getFlightParameters().getDateTime(), destination.getDateTime());

                // Map other nested fields
                map(source.getFlightParameters().getDuration(), destination.getDuration());
                map(source.getFlightParameters().getDescription(), destination.getDescription());

                // Map other UUID fields if needed
                // map(source.getLaunchSpotId(), destination.getLaunchSpotId());
                // map(source.getLandingSpotId(), destination.getLandingSpotId());
                // map(source.getGliderId(), destination.getGliderId());
            }
        });
    }
}