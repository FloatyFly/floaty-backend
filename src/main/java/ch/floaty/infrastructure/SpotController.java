package ch.floaty.infrastructure;

import ch.floaty.domain.model.Spot;
import ch.floaty.domain.model.User;
import ch.floaty.domain.service.ISpotApplicationService;
import ch.floaty.domain.repository.ISpotRepository;
import ch.floaty.generated.SpotDto;
import ch.floaty.generated.SpotCreateDto;
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
public class SpotController {
    private final ISpotApplicationService spotApplicationService;
    private final ModelMapper modelMapper = new ModelMapper();

    public SpotController(ISpotApplicationService spotApplicationService,
                          ISpotRepository spotRepository) {
        this.spotApplicationService = spotApplicationService;
        SpotModelMapperConfig.configure(modelMapper);
    }

    @PostMapping("/spots")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createSpot(@Validated @RequestBody SpotCreateDto spotCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        boolean isLaunchSite = spotCreateDto.getType() == SpotCreateDto.TypeEnum.LAUNCH_SITE ||
                spotCreateDto.getType() == SpotCreateDto.TypeEnum.LAUNCH_AND_LANDING_SITE;
        boolean isLandingSite = spotCreateDto.getType() == SpotCreateDto.TypeEnum.LANDING_SITE ||
                spotCreateDto.getType() == SpotCreateDto.TypeEnum.LAUNCH_AND_LANDING_SITE;

        // Create spot
        Spot spot = spotApplicationService.createSpot(
                user,
                spotCreateDto.getName(),
                spotCreateDto.getLatitude(),
                spotCreateDto.getLongitude(),
                spotCreateDto.getAltitude(),
                spotCreateDto.getDescription(),
                isLaunchSite,
                isLandingSite
        );

        SpotDto responseSpotDto = modelMapper.map(spot, SpotDto.class);
        URI location = URI.create("/spots/" + responseSpotDto.getSpotId());
        log.info("Created spot: ID={}, Name={}, Type={}, Lat={}, Lng={}, Alt={}",
                responseSpotDto.getSpotId(), responseSpotDto.getName(), responseSpotDto.getType(),
                responseSpotDto.getLatitude(), responseSpotDto.getLongitude(), responseSpotDto.getAltitude());
        return ResponseEntity.created(location).body(responseSpotDto);
    }

    @GetMapping("/spots")
    @PreAuthorize("isAuthenticated()")
    public List<SpotDto> getAllSpots(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("Get spots for user {}", user.getUsername());
        return spotApplicationService.findSpotsByUser(user)
                .stream()
                .map(spot -> modelMapper.map(spot, SpotDto.class))
                .collect(toList());
    }

    @GetMapping("/spots/{spotId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<SpotDto> getSpotById(@PathVariable Long spotId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Spot spot = spotApplicationService.findSpotById(spotId);
        if (spot == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if user owns this spot
        if (!spot.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        SpotDto spotDto = modelMapper.map(spot, SpotDto.class);
        return ResponseEntity.ok(spotDto);
    }

    @PutMapping("/spots/{spotId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateSpot(@PathVariable Long spotId,
                                        @Validated @RequestBody SpotCreateDto spotCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Spot existingSpot = spotApplicationService.findSpotById(spotId);
        if (existingSpot == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if user owns this spot
        if (!existingSpot.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean isLaunchSite = spotCreateDto.getType() == SpotCreateDto.TypeEnum.LAUNCH_SITE ||
                spotCreateDto.getType() == SpotCreateDto.TypeEnum.LAUNCH_AND_LANDING_SITE;
        boolean isLandingSite = spotCreateDto.getType() == SpotCreateDto.TypeEnum.LANDING_SITE ||
                spotCreateDto.getType() == SpotCreateDto.TypeEnum.LAUNCH_AND_LANDING_SITE;

        // Update spot
        Spot updatedSpot = spotApplicationService.updateSpot(
                spotId,
                spotCreateDto.getName(),
                spotCreateDto.getLatitude(),
                spotCreateDto.getLongitude(),
                spotCreateDto.getAltitude(),
                spotCreateDto.getDescription(),
                isLaunchSite,
                isLandingSite
        );

        SpotDto responseSpotDto = modelMapper.map(updatedSpot, SpotDto.class);
        log.info("Updated spot: ID={}, Name={}, Type={}",
                responseSpotDto.getSpotId(), responseSpotDto.getName(), responseSpotDto.getType());
        return ResponseEntity.ok(responseSpotDto);
    }

    @DeleteMapping("/spots/{spotId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteSpotById(@PathVariable Long spotId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Spot spot = spotApplicationService.findSpotById(spotId);
        if (spot == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if user owns this spot
        if (!spot.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            spotApplicationService.deleteSpot(spotId);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }

        log.info("Deleted spot: ID={}", spotId);
        return ResponseEntity.noContent().build();
    }
}
