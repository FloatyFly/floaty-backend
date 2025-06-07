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
    public Glider createGlider(User user, String manufacturer, String model) {


        Glider glider = new Glider();
        glider.setUser(user);
        glider.setManufacturer(manufacturer);
        glider.setModel(model);

        Glider savedGlider = gliderRepository.save(glider);
        log.info("Crated glider for user: {}, manufacturer: {}, model: {}", user.getUsername(), manufacturer, model);
        return savedGlider;
    }

    @Override
    public Glider updateGlider(Long gliderId, String manufacturer, String model) {


        Glider glider = findGliderById(gliderId);
        glider.setManufacturer(manufacturer);
        glider.setModel(model);

        Glider updatedGlider = gliderRepository.save(glider);
        log.info("Updated glider ID: {}, manufacturer: {}, model: {}", gliderId, manufacturer, model);
        return updatedGlider;
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