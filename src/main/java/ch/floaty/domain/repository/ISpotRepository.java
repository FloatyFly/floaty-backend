package ch.floaty.domain.repository;

import ch.floaty.domain.model.Spot;
import ch.floaty.domain.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ISpotRepository extends CrudRepository<Spot, Long> {

    Spot findSpotById(Long spotId);
    List<Spot> findSpotsByUser(User user);

}
