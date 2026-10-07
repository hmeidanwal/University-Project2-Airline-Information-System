package io.github.fontysvenlo.ais.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import io.github.fontysvenlo.ais.datarecords.BookingData;
import io.github.fontysvenlo.ais.datarecords.FlightCapacityData;
import io.github.fontysvenlo.ais.persistence.api.BookingRepository;
import io.github.fontysvenlo.ais.persistence.api.SeatNotAvailableException;

public class BookingRepositoryImpl implements BookingRepository {
    private final DataSource db;

    public BookingRepositoryImpl(DBConfig config) {
        this.db = DBProvider.getDataSource(config);
    }

    @Override
    public BookingData createBooking(BookingData booking) throws SeatNotAvailableException {
        if (!isSeatAvailable(booking.flightId(), booking.seatNumber())) {
            throw new SeatNotAvailableException("Seat is taken");
        }

        String sql = "INSERT INTO Booking (FlightID, Price, Luggage, Food, ClassType, SeatNumber, CustomerEmail, CustomerName, Paid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING ID";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, booking.flightId());
            stmt.setBigDecimal(2, booking.price());
            stmt.setBoolean(3, booking.luggage());
            stmt.setBoolean(4, booking.food());
            stmt.setString(5, booking.classType());
            stmt.setString(6, booking.seatNumber());
            stmt.setString(7, booking.customerEmail());
            stmt.setString(8, booking.customerName());
            stmt.setBoolean(9, booking.paid());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new BookingData(id, booking.flightId(), booking.price(), booking.luggage(), booking.food(), booking.classType(), booking.seatNumber(), booking.customerEmail(), booking.customerName(), booking.paid());
            } else {
                throw new SQLException("Failed to insert booking");
            }
        } catch (SQLException e) {
            throw new SeatNotAvailableException("Seat is taken", e);
        }
    }

    @Override
    public boolean isSeatAvailable(int flightId, String seatNumber) throws SeatNotAvailableException {
        String sql = "SELECT COUNT(*) FROM Booking WHERE FlightID = ? AND SeatNumber = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            stmt.setString(2, seatNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0;
            }
            return false;
        } catch (SQLException e) {
            throw new SeatNotAvailableException("Error checking seat availability", e);
        }
    }

    @Override
    public boolean isFlightFull(int flightId) throws SeatNotAvailableException {
        String sql = "SELECT is_flight_full(?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            }
            return false;
        } catch (SQLException e) {
            throw new SeatNotAvailableException("Error checking if flight is full", e);
        }
    }

    @Override
    public int getAvailableSeatsCount(int flightId) throws SeatNotAvailableException {
        String sql = "SELECT get_available_seats_count(?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new SeatNotAvailableException("Error getting available seats count", e);
        }
    }

    @Override
    public List<String> getBookedSeats(int flightId) throws SeatNotAvailableException {
        String sql = "SELECT * FROM get_booked_seats(?)";
        List<String> bookedSeats = new ArrayList<>();
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                bookedSeats.add(rs.getString("seat"));
            }
            return bookedSeats;
        } catch (SQLException e) {
            throw new SeatNotAvailableException("Error getting booked seats", e);
        }
    }

    @Override
    public String suggestNextSeat(int flightId) throws SeatNotAvailableException {
        String sql = "SELECT suggest_next_seat(?)";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString(1); // Returns null if flight is full
            }
            return null;
        } catch (SQLException e) {
            throw new SeatNotAvailableException("Error suggesting next seat", e);
        }
    }

    @Override
    public FlightCapacityData getFlightCapacity(int flightId) throws SeatNotAvailableException {
        String sql = "SELECT * FROM FlightAvailability WHERE FlightID = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, flightId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                List<String> bookedSeats = getBookedSeats(flightId);
                return new FlightCapacityData(
                    rs.getInt("FlightID"),
                    rs.getString("departure"),
                    rs.getString("arrival"),
                    rs.getString("date"),
                    rs.getInt("TotalSeats"),
                    rs.getInt("BookedSeats"),
                    rs.getInt("AvailableSeats"),
                    rs.getBoolean("IsFull"),
                    rs.getDouble("OccupancyPercentage"),
                    bookedSeats
                );
            }
            throw new SeatNotAvailableException("Flight not found: " + flightId);
        } catch (SQLException e) {
            throw new SeatNotAvailableException("Error getting flight capacity", e);
        }
    }
} 