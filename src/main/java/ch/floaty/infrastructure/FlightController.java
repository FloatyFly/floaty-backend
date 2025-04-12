package ch.floaty.infrastructure;

import ch.floaty.domain.model.Flight;
import ch.floaty.domain.model.FlightParameters;
import ch.floaty.domain.model.User;
import ch.floaty.domain.service.IFlightApplicationService;
import ch.floaty.domain.repository.IFlightRepository;
import ch.floaty.domain.repository.IUserRepository;
import ch.floaty.generated.FlightDto;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("Added flight: ID={}, Takeoff={}, Duration={}, Date={}", responseFlightDto.getFlightId(), responseFlightDto.getTakeOff(), responseFlightDto.getDuration(), responseFlightDto.getDateTime());
        return ResponseEntity.created(location).body(responseFlightDto);
    }

    @PutMapping("/flights/{flightId}")
    public ResponseEntity<FlightDto> updateFlight(@PathVariable String flightId, @Validated @RequestBody FlightDto flightDto) {
        UUID flightUUID;
        try {
            flightUUID = UUID.fromString(flightId);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
        Flight flight = flightRepository.findById(flightUUID.toString()).orElse(null);
        if (flight == null) {
            return ResponseEntity.notFound().build();
        }
        LocalDateTime localDateTime;
        try {
            localDateTime = LocalDateTime.parse(flightDto.getDateTime());
        } catch (DateTimeParseException exception) {
            return ResponseEntity.badRequest().build();
        }
        FlightParameters flightParameters = new FlightParameters(
                localDateTime,
                flightDto.getTakeOff(),
                flightDto.getDuration(),
                flightDto.getDescription()
        );
        Flight updatedFlight = flightApplicationService.updateFlight(flightUUID, flightParameters);
        FlightDto responseFlightDto = modelMapper.map(updatedFlight, FlightDto.class);
        log.info("Updated flight: ID={}, Takeoff={}, Duration={}, Date={}", responseFlightDto.getFlightId(), responseFlightDto.getTakeOff(), responseFlightDto.getDuration(), responseFlightDto.getDateTime());
        return ResponseEntity.ok(responseFlightDto);
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
        log.info("Deleted flight: ID={}", flightId);
        return ResponseEntity.noContent().build();
    }
}
