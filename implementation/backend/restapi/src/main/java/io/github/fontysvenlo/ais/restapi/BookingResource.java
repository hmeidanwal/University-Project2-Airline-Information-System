package io.github.fontysvenlo.ais.restapi;

import java.util.Map;

import io.github.fontysvenlo.ais.businesslogic.api.BookingManager;
import io.github.fontysvenlo.ais.datarecords.BookingData;
import io.github.fontysvenlo.ais.datarecords.FlightCapacityData;
import io.github.fontysvenlo.ais.exceptions.InvalidBookingDataException;
import io.github.fontysvenlo.ais.persistence.api.SeatNotAvailableException;
import io.javalin.http.Context;

public class BookingResource {
    private final BookingManager bookingManager;

    public BookingResource(BookingManager bookingManager) {
        this.bookingManager = bookingManager;
    }

    public void create(Context ctx) {
        BookingData booking = ctx.bodyAsClass(BookingData.class);
        try {
            BookingData created = bookingManager.createBooking(booking);
            ctx.status(201).json(created);
        } catch (InvalidBookingDataException e) {
            ctx.status(400).json(Map.of("message", e.getMessage()));
        } catch (SeatNotAvailableException e) {
            ctx.status(409).json(Map.of("message", e.getMessage()));
        }
    }

    public void getFlightCapacity(Context ctx) {
        try {
            int flightId = Integer.parseInt(ctx.pathParam("flightId"));
            FlightCapacityData capacity = bookingManager.getFlightCapacity(flightId);
            ctx.json(capacity);
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("message", "Invalid flight ID"));
        } catch (SeatNotAvailableException e) {
            ctx.status(404).json(Map.of("message", e.getMessage()));
        }
    }

    public void isFlightFull(Context ctx) {
        try {
            int flightId = Integer.parseInt(ctx.pathParam("flightId"));
            boolean isFull = bookingManager.isFlightFull(flightId);
            ctx.json(Map.of("flightId", flightId, "isFull", isFull));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("message", "Invalid flight ID"));
        } catch (SeatNotAvailableException e) {
            ctx.status(500).json(Map.of("message", e.getMessage()));
        }
    }

    public void getAvailableSeatsCount(Context ctx) {
        try {
            int flightId = Integer.parseInt(ctx.pathParam("flightId"));
            int availableSeats = bookingManager.getAvailableSeatsCount(flightId);
            ctx.json(Map.of("flightId", flightId, "availableSeats", availableSeats));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("message", "Invalid flight ID"));
        } catch (SeatNotAvailableException e) {
            ctx.status(500).json(Map.of("message", e.getMessage()));
        }
    }

    public void getBookedSeats(Context ctx) {
        try {
            int flightId = Integer.parseInt(ctx.pathParam("flightId"));
            var bookedSeats = bookingManager.getBookedSeats(flightId);
            ctx.json(Map.of("flightId", flightId, "bookedSeats", bookedSeats));
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("message", "Invalid flight ID"));
        } catch (SeatNotAvailableException e) {
            ctx.status(500).json(Map.of("message", e.getMessage()));
        }
    }

    public void suggestNextSeat(Context ctx) {
        try {
            int flightId = Integer.parseInt(ctx.pathParam("flightId"));
            String suggestedSeat = bookingManager.suggestNextSeat(flightId);
            if (suggestedSeat != null) {
                ctx.json(Map.of("flightId", flightId, "suggestedSeat", suggestedSeat));
            } else {
                ctx.json(Map.of("flightId", flightId, "message", "Flight is full"));
            }
        } catch (NumberFormatException e) {
            ctx.status(400).json(Map.of("message", "Invalid flight ID"));
        } catch (SeatNotAvailableException e) {
            ctx.status(500).json(Map.of("message", e.getMessage()));
        }
    }
} 