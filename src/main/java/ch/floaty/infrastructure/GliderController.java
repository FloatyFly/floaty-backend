package ch.floaty.infrastructure;

import ch.floaty.domain.model.Glider;
import ch.floaty.domain.model.User;
import ch.floaty.domain.service.IGliderApplicationService;
import ch.floaty.generated.GliderCreateDto;
import ch.floaty.generated.GliderDto;
import ch.floaty.generated.GliderUpdateDto;
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
public class GliderController {
    private final IGliderApplicationService gliderApplicationService;
    private final ModelMapper modelMapper = new ModelMapper();

    public GliderController(IGliderApplicationService gliderApplicationService) {
        this.gliderApplicationService = gliderApplicationService;
    }

    @PostMapping("/gliders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createGlider(@Validated @RequestBody GliderCreateDto gliderCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Glider glider = gliderApplicationService.createGlider(
                user,
                gliderCreateDto.getManufacturer(),
                gliderCreateDto.getModel()
        );

        GliderDto responseGliderDto = modelMapper.map(glider, GliderDto.class);
        URI location = URI.create("/gliders/" + responseGliderDto.getId());
        log.info("Created glider: ID={}, Manufacturer={}, Model={}",
                responseGliderDto.getId(), responseGliderDto.getManufacturer(), responseGliderDto.getModel());
        return ResponseEntity.created(location).body(responseGliderDto);
    }

    @GetMapping("/gliders")
    @PreAuthorize("isAuthenticated()")
    public List<GliderDto> getAllGliders(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        log.info("Getting gliders for user {}", user.getUsername());
        return gliderApplicationService.findGlidersByUser(user)
                .stream()
                .map(glider -> modelMapper.map(glider, GliderDto.class))
                .collect(toList());
    }

    @GetMapping("/gliders/{gliderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GliderDto> getGliderById(@PathVariable Long gliderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Glider glider = gliderApplicationService.findGliderById(gliderId);
        if (glider == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if user owns this glider
        if (!glider.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        GliderDto gliderDto = modelMapper.map(glider, GliderDto.class);
        return ResponseEntity.ok(gliderDto);
    }

    @PutMapping("/gliders/{gliderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateGlider(@PathVariable Long gliderId,
                                          @Validated @RequestBody GliderUpdateDto gliderUpdateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Glider existingGlider = gliderApplicationService.findGliderById(gliderId);
        if (existingGlider == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if user owns this glider
        if (!existingGlider.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Update glider - only update fields that were provided
        String manufacturer = gliderUpdateDto.getManufacturer() != null ?
                gliderUpdateDto.getManufacturer() : existingGlider.getManufacturer();
        String model = gliderUpdateDto.getModel() != null ?
                gliderUpdateDto.getModel() : existingGlider.getModel();

        Glider updatedGlider = gliderApplicationService.updateGlider(
                gliderId,
                manufacturer,
                model
        );

        GliderDto responseGliderDto = modelMapper.map(updatedGlider, GliderDto.class);
        log.info("Updated glider: ID={}, Manufacturer={}, Model={}",
                responseGliderDto.getId(), responseGliderDto.getManufacturer(), responseGliderDto.getModel());
        return ResponseEntity.ok(responseGliderDto);
    }

    @DeleteMapping("/gliders/{gliderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteGliderById(@PathVariable Long gliderId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Glider glider = gliderApplicationService.findGliderById(gliderId);
        if (glider == null) {
            return ResponseEntity.notFound().build();
        }

        // Check if user owns this glider
        if (!glider.getUser().equals(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        try {
            gliderApplicationService.deleteGlider(gliderId);
        } catch (EmptyResultDataAccessException e) {
            return ResponseEntity.notFound().build();
        }

        log.info("Deleted glider: ID={}", gliderId);
        return ResponseEntity.noContent().build();
    }
}