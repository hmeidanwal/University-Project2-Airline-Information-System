package io.github.fontysvenlo.ais.datarecords;

public record FlightData(Integer flightNumber, String flightDeparture, String flightDestination, String departureTime,
        String arrivalTime) {
}
