package ch.floaty.infrastructure;

import ch.floaty.domain.model.*;
import ch.floaty.domain.service.IFlightApplicationService;
import ch.floaty.domain.repository.IFlightRepository;
import ch.floaty.domain.repository.IUserRepository;
import ch.floaty.domain.service.IGliderApplicationService;
import ch.floaty.domain.service.ISpotApplicationService;
import ch.floaty.generated.*;
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
    public ResponseEntity<?> createFlight(@Validated @RequestBody FlightCreateDto flightCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        // Launch and landing sites validation
        Spot launchSite = spotApplicationService.findSpotById(flightCreateDto.getLaunchSpotId());
        Spot landingSite = spotApplicationService.findSpotById(flightCreateDto.getLandingSpotId());
        if (launchSite == null || landingSite == null) {
            return ResponseEntity.badRequest().body("Launch or landing site not found");
        }
        if (!launchSite.getUser().equals(user) || !landingSite.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Glider validation
        Glider glider = gliderApplicationService.findGliderById(flightCreateDto.getGliderId());
        if (glider == null) {
            return ResponseEntity.badRequest().build();
        }
        if (!glider.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Flight creation
        FlightParameters flightParameters = new FlightParameters(
                flightCreateDto.getDateTime().toInstant(),
                flightCreateDto.getDuration(),
                flightCreateDto.getDescription(),
                launchSite,
                landingSite,
                glider);

        IgcDataCreate igcDataCreate = null;
        if (flightCreateDto.getIgcDataCreate() != null) {
            FlightCreateIgcDataCreateDto igcDataCreateDto = flightCreateDto.getIgcDataCreate();
            igcDataCreate = new IgcDataCreate(
                    igcDataCreateDto.getFileName(),
                    igcDataCreateDto.getFile());
        }

        Flight flight = this.flightApplicationService.createFlight(user, flightParameters, igcDataCreate);

        FlightDto responseFlightDto = modelMapper.map(flight, FlightDto.class);
        URI location = URI.create("/flights/" + responseFlightDto.getFlightId());
        log.info("Added flight: ID={}, LaunchSite={}, LandingSite={}, Duration={}, Date={}",
                responseFlightDto.getFlightId(), launchSite.getName(), landingSite.getName(), responseFlightDto.getDuration(), responseFlightDto.getDateTime());
        return ResponseEntity.created(location).body(responseFlightDto);
    }

    @PutMapping("/flights/{flightId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FlightDto> updateFlight(@PathVariable Long flightId, @Validated @RequestBody FlightUpdateDto flightUpdateDto) {
        Flight flight = flightRepository.findById(flightId).orElse(null);
        if (flight == null) {
            return ResponseEntity.notFound().build();
        }
        assert flightUpdateDto.getDateTime() != null;
        FlightParameters flightParameters = new FlightParameters(
                flightUpdateDto.getDateTime().toInstant(),
                flightUpdateDto.getDuration(),
                flightUpdateDto.getDescription(),
                spotApplicationService.findSpotById(flightUpdateDto.getLaunchSpotId()),
                spotApplicationService.findSpotById(flightUpdateDto.getLandingSpotId()),
                gliderApplicationService.findGliderById(flightUpdateDto.getGliderId())
        );
        Flight updatedFlight = flightApplicationService.updateFlight(flightId, flightParameters);
        FlightDto responseFlightDto = modelMapper.map(updatedFlight, FlightDto.class);
        log.info("Updated flight: ID={}, Date={}, Launch={}",
                responseFlightDto.getFlightId(), responseFlightDto.getDateTime(), responseFlightDto.getLaunchSpotId());
        return ResponseEntity.ok(responseFlightDto);
    }

    @GetMapping("/flights")
    @PreAuthorize("isAuthenticated()")
    public List<FlightDto> findFlights(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("Get flights for user {}", user.getUsername());
        return flightApplicationService.findFlights(user)
                .stream().map(flight -> modelMapper.map(flight, FlightDto.class)).collect(toList());
    }

    @GetMapping("/flights/{flightId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FlightDto> findFlightById(@PathVariable Long flightId) {
        Flight flight = flightApplicationService.findFlightById(flightId);
        if (flight == null) {
            return ResponseEntity.notFound().build();
        }
        FlightDto flightDto = modelMapper.map(flight, FlightDto.class);
        log.info("Retrieved flight: ID={}, Date={}", flightDto.getFlightId(), flightDto.getDateTime());
        return ResponseEntity.ok(flightDto);
    }

    @DeleteMapping("/flights/{flightId}")
    public ResponseEntity<Void> deleteFlightById(@PathVariable Long flightId) {
        try {
            flightApplicationService.deleteFlight(flightId);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }
        log.info("Deleted flight: ID={}", flightId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/flights/{flightId}/igc")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<IgcDataDto> getIgcData(@PathVariable Long flightId) {
        Flight flight = flightApplicationService.findFlightById(flightId);
        if (flight == null || flight.getIgcData() == null) {
            return ResponseEntity.notFound().build();
        }
        IgcDataDto igcDataDto = modelMapper.map(flight.getIgcData(), IgcDataDto.class);
        log.info("Retrieved IGC data for flight: ID={}", flightId);
        return ResponseEntity.ok(igcDataDto);
    }

    @GetMapping("/flights/{flightId}/track")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FlightTrackDto> getFlightTrack(@PathVariable Long flightId) {
        Flight flight = flightApplicationService.findFlightById(flightId);
        if (flight == null || flight.getIgcData() == null) {
            return ResponseEntity.notFound().build();
        }
        FlightTrack flightTrack = flightApplicationService.getFlightTrack(flightId);
        FlightTrackDto flightTrackDto = modelMapper.map(flightTrack, FlightTrackDto.class);
        log.info("Retrieved flight track for flight: ID={}", flightId);
        return ResponseEntity.ok(flightTrackDto);
    }



}
