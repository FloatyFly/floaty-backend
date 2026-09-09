package ch.floaty.domain.service;

import ch.floaty.domain.model.Glider;
import ch.floaty.domain.model.User;
import ch.floaty.domain.repository.IGliderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class GliderApplicationService implements IGliderApplicationService {

    private final IGliderRepository gliderRepository;

    public GliderApplicationService(IGliderRepository gliderRepository) {
        this.gliderRepository = gliderRepository;
    }

    @Override
    public Glider createGlider(User user, GliderDetails details) {
        Glider glider = new Glider();
        glider.setUser(user);
        applyDetails(glider, details);

        Glider savedGlider = gliderRepository.save(glider);
        log.info("Created glider for user: {}, manufacturer: {}, model: {}, size: {}",
                user.getUsername(), details.getManufacturer(), details.getModel(), details.getSize());
        return savedGlider;
    }

    @Override
    public Glider updateGlider(Long gliderId, GliderDetails details) {
        Glider glider = findGliderById(gliderId);
        applyDetails(glider, details);

        Glider updatedGlider = gliderRepository.save(glider);
        log.info("Updated glider ID: {}, manufacturer: {}, model: {}, size: {}",
                gliderId, details.getManufacturer(), details.getModel(), details.getSize());
        return updatedGlider;
    }

    /**
     * Copies every user-supplied property onto the glider. Nulls are written through rather than
     * skipped: an absent value means the pilot cleared the field, so replacement is the point.
     */
    private void applyDetails(Glider glider, GliderDetails details) {
        glider.setManufacturer(details.getManufacturer());
        glider.setModel(details.getModel());
        glider.setSize(details.getSize());
        glider.setCertificationClass(details.getCertificationClass());
        glider.setGradation(details.getGradation());
    }

    @Override
    @Transactional(readOnly = true)
    public Glider findGliderById(Long gliderId) {
        Optional<Glider> gliderOptional = gliderRepository.findById(gliderId);
        if (gliderOptional.isPresent()) {
            return gliderOptional.get();
        } else {
            log.warn("Glider not found with ID: {}", gliderId);
            return null; // Return null to match controller expectations
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Glider> findGlidersByUser(User user) {
        return gliderRepository.findByUser(user);
    }

    @Override
    public void deleteGlider(Long gliderId) {
        if (!gliderRepository.existsById(gliderId)) {
            log.warn("Attempted to delete non-existent glider with ID: {}", gliderId);
            throw new IllegalArgumentException("Glider not found with ID: " + gliderId);
        }

        gliderRepository.deleteById(gliderId);
        log.info("Deleted glider with ID: {}", gliderId);
    }
}