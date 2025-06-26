package repository;

import domain.ports.TripRepositoryPort;
import infrastructure.configuration.DatabaseConfig;
import domain.model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TripRepository implements TripRepositoryPort {
    private static final Logger logger = LoggerFactory.getLogger(TripRepository.class);
    private static final String INSERT_SQL =
            "INSERT INTO trips(origin, destination, departure_date, departure_time, price, available) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";

    public void save(Trip trip) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setString(1, trip.getOrigin());
                pstmt.setString(2, trip.getDestination());
                pstmt.setString(3, trip.getDepartureDate().toString());
                pstmt.setString(4, trip.getDepartureTime().toString());
                pstmt.setDouble(5, trip.getPrice());
                pstmt.setBoolean(6, trip.getAvailable());
                int affectedRows = pstmt.executeUpdate();
                if (affectedRows > 0) {
                    try (ResultSet rs = pstmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            trip.setId(rs.getLong(1));
                        }
                    }
                }
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed: trips.origin, trips.destination, trips.departure_date, trips.departure_time")) {
                logger.info("Viaje ya insertado - Origen: {}, Destino: {}, Hora {}", trip.getOrigin(), trip.getDestination(), trip.getDepartureTime());
            } else {
                logger.error("Error al guardar hotel en la base de datos", e);
            }
        }
    }

    public List<Trip> findByDestination(String city) throws SQLException {
        List<Trip> trips = new ArrayList<>();
        String sql = "SELECT id, origin, destination, departure_date, departure_time, price, available " +
                "FROM trips WHERE destination = ? ORDER BY departure_date, departure_time";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, city);
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Trip trip = new Trip(
                            rs.getString("origin"),
                            rs.getString("destination"),
                            rs.getString("departure_time"),
                            rs.getString("departure_date"),
                            rs.getDouble("price"),
                            rs.getBoolean("available"));
                        trip.setId(rs.getLong("id"));
                        trips.add(trip);
                    }
                }
            }
        return trips;
    }
}