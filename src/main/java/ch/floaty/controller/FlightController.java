package ch.floaty.controller;

import ch.floaty.domain.*;
import ch.floaty.generated.FlightDto;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RestController
public class FlightController {
    private final IFlightApplicationService flightApplicationService;
    private final ModelMapper modelMapper = new ModelMapper();
    private final IUserRepository userRepository;
    private final IFlightRepository flightRepository;

    public FlightController(IFlightApplicationService flightApplicationService, IUserRepository userRepository, IFlightRepository flightRepository) {
        this.flightApplicationService = flightApplicationService;
        this.userRepository = userRepository;
        this.flightRepository = flightRepository;
        FlightMappingConfig.configure(modelMapper);
    }

    @PostMapping("/flights")
    public ResponseEntity<FlightDto> createFlight(@Validated @RequestBody FlightDto flightDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime.parse(flightDto.getDateTime());
        } catch (DateTimeParseException exception) {
            return ResponseEntity.badRequest().build();
        }
        FlightParameters flightParameters = new FlightParameters(localDateTime, flightDto.getTakeOff(),
                flightDto.getDuration(), flightDto.getDescription());
        Flight flight = this.flightApplicationService.createFlight(user, flightParameters);

        FlightDto responseFlightDto = modelMapper.map(flight, FlightDto.class);
        URI location = URI.create("/flights/" + responseFlightDto.getFlightId());
        System.out.println("Added flight: ID=" + responseFlightDto.getFlightId() + ", Takeoff=" + responseFlightDto.getTakeOff() + ", Duration=" + responseFlightDto.getDuration() + ", Date=" + responseFlightDto.getDateTime());
        return ResponseEntity.created(location).body(responseFlightDto);
    }

    @GetMapping("/flights")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<FlightDto> findAllFlight() {
        return this.flightApplicationService.findAllFlights()
                .stream().map(flight -> modelMapper.map(flight, FlightDto.class)).collect(toList());
    }

    @GetMapping("/flights/{userId}")
    @PreAuthorize("@userSecurity.hasUserIdOrAdmin(#userId)")
    public List<FlightDto> findFlightsForUserId(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return emptyList();
        }
        return flightApplicationService.findFlights(user)
                .stream().map(flight -> modelMapper.map(flight, FlightDto.class)).collect(toList());
    }

    @DeleteMapping("/flights/{flightId}")
    public ResponseEntity<Void> deleteFlightById(@PathVariable String flightId) {
        UUID flightUUID;
        try {
            flightUUID = UUID.fromString(flightId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        try {
            flightApplicationService.deleteFlight(flightUUID);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
