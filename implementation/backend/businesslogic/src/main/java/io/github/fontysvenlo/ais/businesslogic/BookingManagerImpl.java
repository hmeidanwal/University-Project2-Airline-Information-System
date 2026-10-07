package io.github.fontysvenlo.ais.businesslogic;

import java.util.List;

import io.github.fontysvenlo.ais.businesslogic.api.BookingManager;
import io.github.fontysvenlo.ais.datarecords.BookingData;
import io.github.fontysvenlo.ais.datarecords.FlightCapacityData;
import io.github.fontysvenlo.ais.exceptions.InvalidBookingDataException;
import io.github.fontysvenlo.ais.persistence.api.BookingRepository;
import io.github.fontysvenlo.ais.persistence.api.SeatNotAvailableException;

public class BookingManagerImpl implements BookingManager {
    private final BookingRepository bookingRepository;

    public BookingManagerImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    public BookingData createBooking(BookingData booking) throws InvalidBookingDataException, SeatNotAvailableException {
        // Professional validation with proper exceptions
        ValidateCreateBooking(booking);
        
        return bookingRepository.createBooking(booking);
    }

    private void ValidateCreateBooking(BookingData booking) throws InvalidBookingDataException {
        if (booking == null) {
            throw new InvalidBookingDataException("Booking data cannot be null");
        }
        
        if (booking.customerEmail() == null || booking.customerEmail().isBlank()) {
            throw new InvalidBookingDataException("Customer email is required");
        }
        if (booking.seatNumber() == null || booking.seatNumber().isBlank()) {
            throw new InvalidBookingDataException("Seat number is required");
        }
        
        // Professional seat format validation for 60-seat aircraft (rows 1-10, columns A-F)
        if (!booking.seatNumber().matches("^([1-9]|10)[A-F]$")) {
            throw new InvalidBookingDataException("Invalid seat format. Use rows 1-10 and columns A-F (e.g., '5A', '10F', '3C'). Aircraft has 60 seats total.");
        }
        
        // Teacher's example: Name capitalization validation (optional for now)
        if (booking.customerName() != null && !booking.customerName().isBlank()) {
            // Check if name starts with capital letters (First Last format)
            if (!booking.customerName().matches("^[A-Z][a-z]+\\s+[A-Z][a-z]+.*$")) {
                throw new InvalidBookingDataException("Customer name must start with capital letters (e.g., 'John Smith')");
            }
        }
        
        // Payment check
        if (booking.paid() != null && !booking.paid()) {
            throw new InvalidBookingDataException("Booking must be paid");
        }
    }

    @Override
    public boolean isFlightFull(int flightId) throws SeatNotAvailableException {
        return bookingRepository.isFlightFull(flightId);
    }

    @Override
    public int getAvailableSeatsCount(int flightId) throws SeatNotAvailableException {
        return bookingRepository.getAvailableSeatsCount(flightId);
    }

    @Override
    public List<String> getBookedSeats(int flightId) throws SeatNotAvailableException {
        return bookingRepository.getBookedSeats(flightId);
    }

    @Override
    public String suggestNextSeat(int flightId) throws SeatNotAvailableException {
        return bookingRepository.suggestNextSeat(flightId);
    }

    @Override
    public FlightCapacityData getFlightCapacity(int flightId) throws SeatNotAvailableException {
        return bookingRepository.getFlightCapacity(flightId);
    }
} 