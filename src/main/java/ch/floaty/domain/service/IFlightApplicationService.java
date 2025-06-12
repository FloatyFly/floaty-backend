package ch.floaty.domain.service;

import ch.floaty.domain.model.*;

import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;

public interface IFlightApplicationService {
    public Flight createFlight(User user, FlightParameters flightParameters, IgcDataCreate igcDataCreate);
    public List<Flight> findAllFlights();
    public List<Flight> findFlights(User user);
    public Flight updateFlight(Long flightId, FlightParameters flightParameters);
    public void deleteFlight(Long flightId);
    public Flight findFlightById(Long flightId);
    public FlightTrack getFlightTrack(Long flightId);

}
