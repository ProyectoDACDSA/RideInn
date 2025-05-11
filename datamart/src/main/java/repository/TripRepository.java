package repository;

import config.DatabaseConfig;
import model.Trip;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripRepository {
    private static final String INSERT_SQL =
            "INSERT INTO trips(timestamp, origin, destination, departure_time, price, available) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_DESTINATION_SQL =
            "SELECT * FROM trips WHERE destination = ? ORDER BY departure_time";

    public void save(Trip trip) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setLong(1, trip.getTimestamp());
            pstmt.setString(2, trip.getOrigin());
            pstmt.setString(3, trip.getDestination());
            pstmt.setString(4, trip.getDepartureTime().toString());
            pstmt.setDouble(5, trip.getPrice());
            pstmt.setInt(6, trip.getAvailable());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        trip.setId(rs.getLong(1));
                    }
                }
            }

        }
    }

    public List<Trip> findByDestination(String destination) throws SQLException {
        List<Trip> trips = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_DESTINATION_SQL)) {

            pstmt.setString(1, destination);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    long timestamp = rs.getLong("timestamp");
                    String origin = rs.getString("origin");
                    Time departureTime = rs.getTime("departure_time");
                    double price = rs.getDouble("price");
                    int available = rs.getInt("available");
                    Trip trip = new Trip(id, timestamp, origin, destination, departureTime, price, available);
                    trips.add(trip);
                }
            }
        }
        return trips;
    }
}
