package ch.floaty.domain.service;

import ch.floaty.domain.model.Spot;
import ch.floaty.domain.model.User;
import ch.floaty.domain.repository.ISpotRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class SpotApplicationService implements ISpotApplicationService {

    private final ISpotRepository spotRepository;

    @Override
    public Spot createSpot(User user, String name, double latitude, double longitude, double altitude, String description, boolean isLaunchSite, boolean isLandingSite) {
        Spot spot = new Spot();
        spot.setUser(user);
        spot.setName(name);
        spot.setLatitude(latitude);
        spot.setLongitude(longitude);
        spot.setAltitude(altitude);
        spot.setDescription(description);
        spot.setLaunchSite(isLaunchSite);
        spot.setLandingSite(isLandingSite);

        return spotRepository.save(spot);
    }

    @Override
    public Spot updateSpot(Long spotId, String name, double latitude, double longitude, double altitude, String description, boolean isLaunchSite, boolean isLandingSite) {
        Spot spot = spotRepository.findSpotById(spotId);
        if (spot == null) {
            throw new IllegalArgumentException("Spot to update not found with ID: " + spotId);
        }

        spot.setName(name);
        spot.setLatitude(latitude);
        spot.setLongitude(longitude);
        spot.setDescription(description);
        spot.setLaunchSite(isLaunchSite);
        spot.setLandingSite(isLandingSite);

        return spotRepository.save(spot);
    }

    @Override
    public Spot findSpotById(Long spotId) {
        Spot spot = spotRepository.findSpotById(spotId);
        if (spot == null) {
            throw new IllegalArgumentException("Spot not found with ID: " + spotId);
        }
        return spot;
    }

    @Override
    public List<Spot> findSpotsByUser(User user) {
        return spotRepository.findSpotsByUser(user);
    }

    @Override
    public void deleteSpot(Long spotId) {
        Spot spot = spotRepository.findSpotById(spotId);
        if (spot == null) {
            throw new IllegalArgumentException("Spot to delete not found with ID: " + spotId);
        }
        spotRepository.delete(spot);
    }

}
