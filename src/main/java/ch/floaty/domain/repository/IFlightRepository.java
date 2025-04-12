package ch.floaty.domain.repository;

import ch.floaty.domain.model.Flight;
import ch.floaty.domain.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IFlightRepository extends CrudRepository<Flight, String> {

    List<Flight> findByUser(User user);


}
