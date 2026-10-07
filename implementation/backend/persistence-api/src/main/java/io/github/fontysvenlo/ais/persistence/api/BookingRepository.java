package io.github.fontysvenlo.ais.persistence.api;

import io.github.fontysvenlo.ais.datarecords.BookingData;
import io.github.fontysvenlo.ais.datarecords.FlightCapacityData;
import java.util.List;

public interface BookingRepository {
    BookingData createBooking(BookingData booking) throws SeatNotAvailableException;

    /**
     * Checks if a seat is available for a given flight.
     * @param flightId the flight ID
     * @param seatNumber the seat number
     * @return true if the seat is available, false otherwise
     * @throws SeatNotAvailableException if there is an error checking seat availability
     */
    boolean isSeatAvailable(int flightId, String seatNumber) throws SeatNotAvailableException;
    
    /**
     * Checks if a flight is full (no more seats available).
     * @param flightId the flight ID
     * @return true if the flight is full, false otherwise
     * @throws SeatNotAvailableException if there is an error checking flight capacity
     */
    boolean isFlightFull(int flightId) throws SeatNotAvailableException;
    
    /**
     * Gets the number of available seats for a flight.
     * @param flightId the flight ID
     * @return the number of available seats
     * @throws SeatNotAvailableException if there is an error checking availability
     */
    int getAvailableSeatsCount(int flightId) throws SeatNotAvailableException;
    
    /**
     * Gets all booked seats for a flight in sorted order.
     * @param flightId the flight ID
     * @return list of booked seat numbers (e.g., ["1A", "1B", "2C"])
     * @throws SeatNotAvailableException if there is an error retrieving booked seats
     */
    List<String> getBookedSeats(int flightId) throws SeatNotAvailableException;
    
    /**
     * Suggests the next available seat for a flight (smart assignment).
     * @param flightId the flight ID
     * @return the next available seat number, or null if flight is full
     * @throws SeatNotAvailableException if there is an error suggesting a seat
     */
    String suggestNextSeat(int flightId) throws SeatNotAvailableException;
    
    /**
     * Gets complete flight capacity information.
     * @param flightId the flight ID
     * @return FlightCapacityData with all capacity details
     * @throws SeatNotAvailableException if there is an error retrieving capacity data
     */
    FlightCapacityData getFlightCapacity(int flightId) throws SeatNotAvailableException;
} 