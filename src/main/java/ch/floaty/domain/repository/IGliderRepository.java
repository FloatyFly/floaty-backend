package ch.floaty.domain.repository;

import ch.floaty.domain.model.Glider;
import ch.floaty.domain.model.Spot;
import ch.floaty.domain.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IGliderRepository extends CrudRepository<Glider, Long> {
    List<Glider> findByUser(User user
    );
}
