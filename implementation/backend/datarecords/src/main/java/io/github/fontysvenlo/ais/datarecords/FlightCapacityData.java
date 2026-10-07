package io.github.fontysvenlo.ais.datarecords;

import java.util.List;

/**
 * Record containing complete flight capacity information.
 */
public record FlightCapacityData(
    Integer flightId,
    String departure,
    String arrival,
    String date,
    Integer totalSeats,
    Integer bookedSeats,
    Integer availableSeats,
    Boolean isFull,
    Double occupancyPercentage,
    List<String> bookedSeatNumbers
) {} 