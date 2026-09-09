package ch.floaty.domain.service;

import ch.floaty.domain.model.Glider;
import ch.floaty.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface IGliderApplicationService {
    public Glider createGlider(User user, GliderDetails details);
    public Glider updateGlider(Long gliderId, GliderDetails details);
    public Glider findGliderById(Long gliderId);
    public List<Glider> findGlidersByUser(User user);
    public void deleteGlider(Long gliderId);
}
