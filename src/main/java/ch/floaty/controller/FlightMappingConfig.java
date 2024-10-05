package ch.floaty.controller;

import ch.floaty.domain.Flight;
import ch.floaty.generated.FlightDto;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;

import java.time.LocalDateTime;

public class FlightMappingConfig {

    public static void configure(ModelMapper modelMapper) {
        modelMapper.addConverter(new AbstractConverter<LocalDateTime, String>() {
            protected String convert(LocalDateTime source) {
                return source.toString();
            }
        });
        modelMapper.addMappings(new PropertyMap<Flight, FlightDto>() {
            @Override
            protected void configure() {
                map().setFlightId(source.getId());
                map().setUserId(source.getUser().getId());
                using(ctx -> ((Flight) ctx.getSource()).getFlightParameters().getDateTime().toString()).map(source, destination.getDateTime());
                map().setTakeOff(source.getFlightParameters().getTakeOff());
                map().setDuration(source.getFlightParameters().getDuration());
                map().setDescription(source.getFlightParameters().getDescription());
            }
        });
    }
}
