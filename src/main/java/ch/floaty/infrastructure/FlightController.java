package ch.floaty.infrastructure;

import ch.floaty.domain.model.*;
import ch.floaty.domain.service.IFlightApplicationService;
import ch.floaty.domain.repository.IFlightRepository;
import ch.floaty.domain.repository.IUserRepository;
import ch.floaty.domain.service.IGliderApplicationService;
import ch.floaty.domain.service.ISpotApplicationService;
import ch.floaty.generated.FlightDto;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@RestController
@Slf4j
public class FlightController {
    private final IFlightApplicationService flightApplicationService;
    private final ISpotApplicationService spotApplicationService;
    private final IGliderApplicationService gliderApplicationService;
    private final ModelMapper modelMapper = new ModelMapper();
    private final IFlightRepository flightRepository;

    public FlightController(IFlightApplicationService flightApplicationService,
                            ISpotApplicationService spotApplicationService,
                            IGliderApplicationService gliderApplicationService,
                            IUserRepository userRepository,
                            IFlightRepository flightRepository) {
        this.flightApplicationService = flightApplicationService;
        this.spotApplicationService = spotApplicationService;
        this.gliderApplicationService = gliderApplicationService;
        this.flightRepository = flightRepository;
        FlightMappingConfig.configure(modelMapper);
    }

    @PostMapping("/flights")
    public ResponseEntity<?> createFlight(@Validated @RequestBody FlightDto flightDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // Launch and landing sites validation
        Spot launchSite = spotApplicationService.findSpotById(flightDto.getLaunchSpotId());
        Spot landingSite = spotApplicationService.findSpotById(flightDto.getLandingSpotId());
        if (launchSite == null || landingSite == null) {
            return ResponseEntity.badRequest().body("Launch or landing site not found");
        }
        if (!launchSite.getUser().equals(user) || !landingSite.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Glider validation
        Glider glider = gliderApplicationService.findGliderById(flightDto.getGliderId());
        if (glider == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!glider.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Flight creation
        FlightParameters flightParameters = new FlightParameters(
                flightDto.getDateTime().toInstant(),
                flightDto.getDuration(),
                flightDto.getDescription(),
                launchSite,
                landingSite);
        Flight flight = this.flightApplicationService.createFlight(user, flightParameters);

        FlightDto responseFlightDto = modelMapper.map(flight, FlightDto.class);
        URI location = URI.create("/flights/" + responseFlightDto.getFlightId());
        log.info("Added flight: ID={}, LaunchSite={}, LandingSite={}, Duration={}, Date={}",
                responseFlightDto.getFlightId(), launchSite.getName(), landingSite.getName(), responseFlightDto.getDuration(), responseFlightDto.getDateTime());
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
        FlightParameters flightParameters = new FlightParameters(
                flightDto.getDateTime().toInstant(),
                flightDto.getDuration(),
                flightDto.getDescription(),
                spotApplicationService.findSpotById(flightDto.getLaunchSpotId()),
                spotApplicationService.findSpotById(flightDto.getLandingSpotId())
        );
        Flight updatedFlight = flightApplicationService.updateFlight(flightUUID, flightParameters);
        FlightDto responseFlightDto = modelMapper.map(updatedFlight, FlightDto.class);
        log.info("Updated flight: ID={}, Date={}, Launch={}",
                responseFlightDto.getFlightId(), responseFlightDto.getDateTime(), responseFlightDto.getLaunchSpotId());
        return ResponseEntity.ok(responseFlightDto);
    }

    @GetMapping("/flights")
    @PreAuthorize("isAuthenticated()")
    public List<FlightDto> findFlights(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
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
