package io.github.fontysvenlo.ais.businesslogic;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import io.github.fontysvenlo.ais.businesslogic.api.FlightManager;
import io.github.fontysvenlo.ais.datarecords.FlightData;
import io.github.fontysvenlo.ais.persistence.api.FlightRepository;

/**
 * FlightManager handles flight operations.
 */
public class FlightManagerImpl implements FlightManager {
    private final FlightRepository flightRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    public FlightManagerImpl(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    /**
     * Search flights based on departure and arrival.
     *
     * @param departure Departure location.
     * @param arrival Arrival location.
     *
     * @return List of matching flights.
     */
    @Override
    public List<List<FlightData>> search(String departure, String arrival, String datetime) {
        //it shoudl be checked if the data is correct for example same departure and arrival
        departure = departure.toLowerCase();
        arrival = arrival.toLowerCase();
        if (departure.equals(arrival)) {
        throw new InvalidFlightSearchException("Departure and arrival cannot be the same.");
        }

        if (departure.isBlank()) {
            throw new InvalidFlightSearchException("Departure cannot be empty.");
        }

        if (arrival.isBlank()) {
            throw new InvalidFlightSearchException("Arrival cannot be empty.");
        }

        if (datetime == null || datetime.isBlank()) {
            throw new InvalidFlightSearchException("Datetime cannot be empty.");
        }

        OffsetDateTime offsetDateTime = OffsetDateTime.parse(datetime, FORMATTER);
LocalDateTime dateTimeParsed = offsetDateTime.toLocalDateTime();

        if (!dateTimeParsed.isAfter(LocalDateTime.now())) {
            throw new InvalidFlightSearchException("Date and time has to be in the future.");
        }
    

        

        return flightRepository.search(departure, arrival, datetime);
    }
}
