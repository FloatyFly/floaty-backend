package ch.floaty.domain.service;

import ch.floaty.domain.model.Flight;
import ch.floaty.domain.model.FlightParameters;
import ch.floaty.domain.model.User;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

public interface IFlightApplicationService {
    public Flight createFlight(User user, FlightParameters flightParameters) throws DateTimeParseException;
    public List<Flight> findAllFlights();
    public List<Flight> findFlights(User user);
    public void deleteFlight(UUID flightId);

}
