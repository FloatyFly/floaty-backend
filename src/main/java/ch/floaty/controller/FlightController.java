package ch.floaty.controller;

import ch.floaty.domain.*;
import ch.floaty.generated.FlightDto;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

@RestController
public class FlightController {
    private final IFlightRepository flightRepository;
    private final IUserRepository userRepository;
    private ModelMapper modelMapper = new ModelMapper();

    public FlightController(IFlightRepository flightRepository, IUserRepository userRepository) {
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/flights")
    public List<FlightDto> findAllFlight() {
        Iterable<Flight> flights = flightRepository.findAll();
        return StreamSupport.stream(flights.spliterator(), false)
                .map(flight -> modelMapper.map(flight, FlightDto.class))
                .collect(toList());
    }

    @GetMapping("/flights/{userId}")
    public List<FlightDto> findFlightsByUser(@PathVariable Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    List<Flight> flights = flightRepository.findByUser(user);
                    return flights.stream()
                            .map(flight -> modelMapper.map(flight, FlightDto.class))
                            .collect(Collectors.toList());
                })
                .orElse(emptyList());
    }

    @PostMapping("/flights")
    public ResponseEntity<FlightDto> saveFlight(@Validated @RequestBody FlightDto flightDto) {
        Optional<User> user = userRepository.findById(flightDto.getUserId());
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Flight flight = new Flight(user.get(), flightDto.getTakeoff(), flightDto.getDuration(), flightDto.getDate(),
                flightDto.getDescription() != null ? flightDto.getDescription() : "");
        String flightId = UUID.randomUUID().toString();
        flight.setId(flightId);
        Flight responseFlight = flightRepository.save(flight);
        FlightDto responseFlightDto = modelMapper.map(responseFlight, FlightDto.class);
        URI location = URI.create("/flights/" + responseFlightDto.getFlightId());
        System.out.println("Added flight: ID=" + responseFlightDto.getFlightId() + ", Takeoff=" + responseFlightDto.getTakeoff() + ", Duration=" + responseFlightDto.getDuration() + ", Date=" + responseFlightDto.getDate());
        return ResponseEntity.created(location).body(responseFlightDto);
    }

    @DeleteMapping("/flights/{flightId}")
    public ResponseEntity<Void> deleteFlightById(@PathVariable String flightId) {
        if (flightRepository.existsById(flightId)) {
            flightRepository.deleteById(flightId);
            System.out.println("Deleted flight.");
            return ResponseEntity.ok().build();  // Return 200 OK
        } else {
            return ResponseEntity.notFound().build();  // Return 404 Not Found if the flight doesn't exist
        }
    }

}
