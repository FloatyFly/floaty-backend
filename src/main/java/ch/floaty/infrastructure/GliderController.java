package ch.floaty.infrastructure;

import ch.floaty.domain.model.CertificationClass;
import ch.floaty.domain.model.Glider;
import ch.floaty.domain.model.Gradation;
import ch.floaty.domain.model.User;
import ch.floaty.domain.service.GliderDetails;
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
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@Slf4j
public class GliderController {
    private final IGliderApplicationService gliderApplicationService;
    private final ModelMapper modelMapper = new ModelMapper();

    public GliderController(IGliderApplicationService gliderApplicationService) {
        this.gliderApplicationService = gliderApplicationService;
        GliderMappingConfig.configure(this.modelMapper);
    }

    @PostMapping("/gliders")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createGlider(@Validated @RequestBody GliderCreateDto gliderCreateDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        if (gliderCreateDto.getGradation() != null && gliderCreateDto.getCertificationClass() == null) {
            return gradationWithoutClass();
        }

        Glider glider = gliderApplicationService.createGlider(
                user,
                new GliderDetails(
                        gliderCreateDto.getManufacturer(),
                        gliderCreateDto.getModel(),
                        gliderCreateDto.getSize(),
                        toCertificationClass(gliderCreateDto.getCertificationClass() == null
                                ? null : gliderCreateDto.getCertificationClass().getValue()),
                        toGradation(gliderCreateDto.getGradation() == null
                                ? null : gliderCreateDto.getGradation().getValue())
                )
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

        if (gliderUpdateDto.getGradation() != null && gliderUpdateDto.getCertificationClass() == null) {
            return gradationWithoutClass();
        }

        // PUT replaces the glider: an absent field means the pilot cleared it, not "keep the
        // existing value". Clients must send a complete representation.
        Glider updatedGlider = gliderApplicationService.updateGlider(
                gliderId,
                new GliderDetails(
                        gliderUpdateDto.getManufacturer(),
                        gliderUpdateDto.getModel(),
                        gliderUpdateDto.getSize(),
                        toCertificationClass(gliderUpdateDto.getCertificationClass() == null
                                ? null : gliderUpdateDto.getCertificationClass().getValue()),
                        toGradation(gliderUpdateDto.getGradation() == null
                                ? null : gliderUpdateDto.getGradation().getValue())
                )
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

    /**
     * A gradation such as "High" only means something alongside a certification class, so a
     * gradation with no class is rejected rather than stored as an orphan.
     */
    private ResponseEntity<Object> gradationWithoutClass() {
        return ResponseEntity.badRequest()
                .body(Map.of("message", "gradation requires a certificationClass to be set."));
    }

    private CertificationClass toCertificationClass(String value) {
        return value == null ? null : CertificationClass.valueOf(value);
    }

    private Gradation toGradation(String value) {
        return value == null ? null : Gradation.valueOf(value);
    }
}
