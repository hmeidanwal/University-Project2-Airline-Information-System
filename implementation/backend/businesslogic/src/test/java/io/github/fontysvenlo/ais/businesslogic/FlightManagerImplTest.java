package io.github.fontysvenlo.ais.businesslogic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.fontysvenlo.ais.datarecords.FlightData;
import io.github.fontysvenlo.ais.persistence.api.FlightRepository;

class FlightManagerImplTest {
    private FlightRepository flightRepository;
    private FlightManagerImpl flightManager;

    @BeforeEach
    void setUp() {
        flightRepository = Mockito.mock(FlightRepository.class);
        flightManager = new FlightManagerImpl(flightRepository);
    }

    @Test
    void testSearchFlightsValid() {
        when(flightRepository.search("amsterdam", "london", "2025-07-01T08:00:00Z"))
                .thenReturn(java.util.Collections.singletonList(List.of(
                        new FlightData(1, "Amsterdam", "London", "2025-07-01T08:00:00Z", "2025-07-01T10:00:00Z"))));
        List<List<FlightData>> search = flightManager.search("Amsterdam", "London", "2025-07-01T08:00:00Z");
        assertNotNull(search, "Search result should not be null");
        assertEquals(1, search.size(), "Search result should contain one flight");
    }

    @Test
    void testSearchFlightsNoResults() {
        when(flightRepository.search("miami", "berlin", "2025-07-01T08:00:00Z")).thenReturn(List.of());
        List<List<FlightData>> search = flightManager.search("Miami", "Berlin", "2025-07-01T08:00:00Z");

        assertNotNull(search, "Search result should not be null");
        assertTrue(search.isEmpty(), "Search result should be empty for no matches");
    }

    @Test
    void testSearchFlightsSameDeaprtureAndArrival() {
        when(flightRepository.search("amsterdam", "amsterdam", "2025-07-01T08:00:00Z")).thenReturn(List.of());
        InvalidFlightSearchException exception = assertThrows(InvalidFlightSearchException.class, () -> {
            flightManager.search("Amsterdam", "Amsterdam", "2025-07-01T08:00:00Z");
        });
        assertEquals("Departure and arrival cannot be the same.", exception.getMessage());
        verify(flightRepository, never()).search(any(), any(), any());
    }

    @Test
    void testSearchFlightsBlankDeparture() {
        InvalidFlightSearchException exception = org.junit.jupiter.api.Assertions
                .assertThrows(InvalidFlightSearchException.class, () -> {
                    flightManager.search("", "London", "2025-07-01T08:00:00Z");
                });
        assertEquals("Departure cannot be empty.", exception.getMessage());
        verify(flightRepository, never()).search(any(), any(), any());
    }

    @Test
    void testSearchFlightsBlankArrival() {
        InvalidFlightSearchException exception = org.junit.jupiter.api.Assertions
                .assertThrows(InvalidFlightSearchException.class, () -> {
                    flightManager.search("Amsterdam", "", "2025-07-01T08:00:00Z");
                });
        assertEquals("Arrival cannot be empty.", exception.getMessage());
        verify(flightRepository, never()).search(any(), any(), any());
    }

    @Test
    void testSearchFlightsBlankDatetime() {
        InvalidFlightSearchException exception = org.junit.jupiter.api.Assertions
                .assertThrows(InvalidFlightSearchException.class, () -> {
                    flightManager.search("Amsterdam", "London", "");
                });
        assertEquals("Datetime cannot be empty.", exception.getMessage());
        verify(flightRepository, never()).search(any(), any(), any());
    }

    @Test
    void testSearchFlightsPastDatetime() {
        InvalidFlightSearchException exception = org.junit.jupiter.api.Assertions
                .assertThrows(InvalidFlightSearchException.class, () -> {
                    flightManager.search("Amsterdam", "London", "2020-01-01T08:00:00Z");
                });
        assertEquals("Date and time has to be in the future.", exception.getMessage());
        verify(flightRepository, never()).search(any(), any(), any());
    }

}