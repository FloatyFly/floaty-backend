package ch.floaty.domain.service;

import ch.floaty.domain.model.Spot;
import ch.floaty.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface ISpotApplicationService {
    public Spot createSpot(User user, String name, double latitude, double longitude, double altitude, String description, boolean isLaunchSite, boolean isLandingSite);
    public Spot updateSpot(Long spotId, String name, double latitude, double longitude, double altitude, String description, boolean isLaunchSite, boolean isLandingSite);
    public Spot findSpotById(Long spotId);
    public List<Spot> findSpotsByUser(User user);
    public void deleteSpot(Long spotId);
}
