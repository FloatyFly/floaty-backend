package ch.floaty.domain;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Service
public class FlightApplicationService implements IFlightApplicationService {

    private final IFlightRepository flightRepository;

    @Override
    public Flight createFlight(User user, FlightParameters flightParameters) throws DateTimeParseException {
        UUID flightId = UUID.randomUUID();
        Flight flight = new Flight(flightId.toString(), user, flightParameters);
        return flightRepository.save(flight);
    }

    @Override
    public List<Flight> findAllFlights() {
        return StreamSupport.stream(flightRepository.findAll().spliterator(), false).collect(toList());
    }

    @Override
    public List<Flight> findFlights(User user) {
        return flightRepository.findByUser(user);
    }

    @Override
    public void deleteFlight(UUID flightUUID) throws EmptyResultDataAccessException {
        flightRepository.deleteById(flightUUID.toString());
    }
}
