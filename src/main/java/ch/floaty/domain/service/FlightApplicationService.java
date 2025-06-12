package ch.floaty.domain.service;

import ch.floaty.domain.model.*;
import ch.floaty.domain.repository.IFlightRepository;
import ch.floaty.domain.repository.IIgcDataRepository;
import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static java.util.stream.Collectors.toList;

@AllArgsConstructor
@Service
@Transactional
public class FlightApplicationService implements IFlightApplicationService {

    private final IFlightRepository flightRepository;
    private final IIgcDataRepository igcDataRepository;

    @Override
    public Flight createFlight(User user, FlightParameters flightParameters, IgcDataCreate igcDataCreate) {
        Flight flight = new Flight();
        flight.setUser(user);
        flight.setFlightParameters(flightParameters);
        if (igcDataCreate != null) {
            IgcData igcData = new IgcData();
            IgcMetadata igcMetadata = new IgcMetadata();
            igcMetadata.setUploadedAt(LocalDateTime.now().toInstant(java.time.ZoneOffset.UTC));
            igcMetadata.setFileSize((long) igcDataCreate.getFile().length);
            // TODO: Fix the checksum.
            igcMetadata.setChecksum(UUID.randomUUID().toString());
            igcMetadata.setFileName(igcDataCreate.getFileName());
            igcData.setIgcMetadata(igcMetadata);
            // TODO: Add validity check.
            igcData.setFile(igcDataCreate.getFile());

            this.igcDataRepository.save(igcData);
            flight.setIgcData(igcData);
        }

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
    public Flight updateFlight(Long flightId, FlightParameters flightParameters) {
        Flight flight = flightRepository.findById(flightId).orElseThrow(() -> new IllegalArgumentException("Flight not found"));
        flight.setFlightParameters(flightParameters);
        return flightRepository.save(flight);
    }

    @Override
    public void deleteFlight(Long flightId) throws EmptyResultDataAccessException {
        flightRepository.deleteById(flightId);
    }

    @Override
    public Flight findFlightById(Long flightId) {
        return flightRepository.findById(flightId).orElseThrow(() -> new IllegalArgumentException("Flight not found"));
    }

    @Override
    public FlightTrack getFlightTrack(Long flightId) {
        Flight flight = flightRepository.findById(flightId).orElseThrow(() -> new IllegalArgumentException("Flight not found"));
        if (flight.getIgcData() == null) {
            throw new IllegalArgumentException("Flight does not have IGC data");
        }
        return new FlightTrack(flightId, flight.getIgcData());
    }
}
