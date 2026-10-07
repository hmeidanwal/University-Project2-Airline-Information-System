package io.github.fontysvenlo.ais.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import io.github.fontysvenlo.ais.datarecords.FlightData;
import io.github.fontysvenlo.ais.persistence.api.FlightRepository;



class FlightRepositoryImpl implements FlightRepository {

    private final DataSource db;

    private static final ZoneId ZONE = ZoneId.of("Europe/Amsterdam");
private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public FlightRepositoryImpl(DBConfig config) {
        this.db = DBProvider.getDataSource(config);
    }

    @Override
    public List<List<FlightData>> search(String departure, String arrival, String datetimeStr) {
        List<List<FlightData>> resultPaths = new ArrayList<>();

        Timestamp datetime;
        try {
            Instant instant = Instant.parse(datetimeStr); 
ZonedDateTime zoned = instant.atZone(ZoneId.of("Europe/Amsterdam"));
datetime = Timestamp.from(zoned.toInstant());
        } catch (IllegalArgumentException e) {
            throw new FlightDataAccessException("Invalid datetime format. Expected 'yyyy-MM-dd HH:mm:ss'", e);
        }

        resultPaths.addAll(getTransferFlights(departure, arrival, datetime));

        return resultPaths;
    }


    private List<List<FlightData>> getTransferFlights(String departure, String arrival, Timestamp datetime) {
        List<List<FlightData>> flights = new ArrayList<>();
        String sql =
            "WITH RECURSIVE flight_paths AS (" +
            "   SELECT f1.id, f1.departure, f1.arrival, f1.departuretime as min_departure, f1.arrivaltime, ARRAY[f1.id] AS path, f1.departure AS first_departure " +
            "   FROM flights f1 " +
            "   WHERE f1.departure = ? AND f1.departuretime >= ? " +
            "   UNION ALL " +
            "   SELECT f2.id, f2.departure, f2.arrival, f2.departuretime, f2.arrivaltime, fp.path || f2.id, fp.first_departure " +
            "   FROM flights f2 " +
            "   JOIN flight_paths fp ON f2.departure = fp.arrival " +
            "   WHERE f2.departuretime >= fp.arrivaltime " +
            "AND f2.departuretime >= fp.min_departure" +
            "   AND array_length(fp.path, 1) < 6 " +
            ") " +
            "SELECT fp.path FROM flight_paths fp WHERE fp.arrival = ?";

        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, departure);
            stmt.setTimestamp(2, datetime);
            stmt.setString(3, arrival);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Integer[] pathIds = (Integer[]) rs.getArray("path").getArray();

                    List<FlightData> fullPath = new ArrayList<>();
                    for (int id : pathIds) {
                        fullPath.add(getFlightById(id));
                    }
                    System.out.println("Full flight path: " + fullPath);

                    flights.add(fullPath);
                }
            }

        } catch (SQLException e) {
            throw new FlightDataAccessException("Error retrieving flight by ID ", e);
        }

        return flights;
    }

    private FlightData getFlightById(int id) throws SQLException {
        String sql = "SELECT id, departure, arrival, departuretime, arrivaltime FROM flights WHERE id = ?";
        try (Connection conn = db.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new FlightData(
                        rs.getInt("id"),
                        rs.getString("departure"),
                        rs.getString("arrival"),
                        rs.getTimestamp("departuretime").toInstant().atZone(ZONE).toLocalDateTime().format(FORMATTER),
    rs.getTimestamp("arrivaltime").toInstant().atZone(ZONE).toLocalDateTime().format(FORMATTER)
                    );
                } else {
                    throw new FlightDataAccessException("Error retrieving flight");
                }
            } catch (SQLException e) {
                throw new FlightDataAccessException("Error retrieving flight by ID ", e);
            }
        }
    }
}
