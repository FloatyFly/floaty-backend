package ch.floaty.domain.service;

import ch.floaty.domain.model.Glider;
import ch.floaty.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface IGliderApplicationService {
    public Glider createGlider(User user, String manufacturer, String model);
    public Glider updateGlider(Long gliderId, String manufacturer, String model);
    public Glider findGliderById(Long gliderId);
    public List<Glider> findGlidersByUser(User user);
    public void deleteGlider(Long gliderId);
}
