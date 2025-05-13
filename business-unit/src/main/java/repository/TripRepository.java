package repository;

import config.DatabaseConfig;
import model.Trip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TripRepository {
    private static final Logger logger = LoggerFactory.getLogger(TripRepository.class);
    private static final String INSERT_SQL =
            "INSERT INTO trips(origin, destination, departure_date, departure_time, price, available) " +
                    "VALUES(?, ?, ?, ?, ?, ?)";

    private static final String SELECT_BY_DESTINATION_SQL =
            "SELECT * FROM trips WHERE destination = ? ORDER BY departure_time";

    public void save(Trip trip) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, trip.getOrigin());
            pstmt.setString(2, trip.getDestination());
            pstmt.setString(3, trip.getDepartureDate().toString());
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

        } catch (SQLException e) {
        if (e.getMessage().contains("UNIQUE constraint failed: trips.origin, trips.destination, trips.departure_date, trips.departure_time")) {
            logger.info("Viaje ya insertado - Origen: {}, Destino: {}, Hora {}", trip.getOrigin(), trip.getDestination(), trip.getDepartureTime());
        } else {
            logger.error("Error al guardar hotel en la base de datos", e);
        }
    }}

    public List<Trip> findByDestinationAndDateRange(String destination, LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = "SELECT * FROM trips WHERE destination = ? AND departure_date BETWEEN ? AND ?";
        return List.of();
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
                            rs.getInt("available"));
                    trip.setId(rs.getLong("id"));
                    trips.add(trip);
                }
            }
        }
        return trips;
    }
}

