package repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import config.DatabaseConfig;
import java.sql.*;
import java.time.LocalDate;

public class TravelPackageRepository {
    private static final Logger logger = LoggerFactory.getLogger(TravelPackageRepository.class);
    private static final String INSERT_SQL =
            "INSERT INTO travel_packages(city, trip_id, hotel_id, trip_date, " +
                    "hotel_check_in, hotel_check_out, total_price) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?)";

    private static final String FIND_BY_CITY_SQL =
            "SELECT tp.*, t.origin, t.destination, t.departure_time, " +
                    "h.hotel_name, h.accommodation_type, h.rating " +
                    "FROM travel_packages tp " +
                    "JOIN trips t ON tp.trip_id = t.id " +
                    "JOIN hotels h ON tp.hotel_id = h.id " +
                    "WHERE tp.city = ? " +
                    "ORDER BY tp.total_price";

    public void savePackage(String city, long tripId, long hotelId,
                            LocalDate tripDate, LocalDate checkIn,
                            LocalDate checkOut, double totalPrice) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL)) {

            pstmt.setString(1, city);
            pstmt.setLong(2, tripId);
            pstmt.setLong(3, hotelId);
            pstmt.setString(4, tripDate.toString());
            pstmt.setString(5, checkIn.toString());
            pstmt.setString(6, checkOut.toString());
            pstmt.setDouble(7, totalPrice);

            try {
                pstmt.executeUpdate();
            } catch (SQLException e) {
                if (e.getMessage().contains("UNIQUE constraint failed: travel_packages.trip_id, travel_packages.hotel_id, travel_packages.trip_date")) {
                    logger.debug("Paquete ya existente - Viaje: {}, Hotel: {}, Fecha: {}",
                            tripId, hotelId, tripDate);
                } else {
                    logger.error("Error al guardar paquete en la base de datos", e);
                }
            }
        }
    }

    public void printPackagesForCity(String city) throws SQLException {
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(FIND_BY_CITY_SQL)) {

            pstmt.setString(1, city);

            try (ResultSet rs = pstmt.executeQuery()) {
                System.out.printf("\nPaquetes disponibles en %s:%n", city);
                System.out.println("--------------------------------------------------");
                while (rs.next()) {
                    System.out.printf(
                            "Viaje: %s a %s (%s a las %s)%n" +
                                    "Hotel: %s (%s) - Rating: %.1f%n" +
                                    "Estadía: %s a %s | Precio total: %.2f€%n" +
                                    "--------------------------------------------------%n",
                            rs.getString("origin"),
                            rs.getString("destination"),
                            rs.getString("trip_date"),
                            rs.getString("departure_time"),
                            rs.getString("hotel_name"),
                            rs.getString("accommodation_type"),
                            rs.getDouble("rating"),
                            rs.getString("hotel_check_in"),
                            rs.getString("hotel_check_out"),
                            rs.getDouble("total_price"));
                }
            }
        }
    }
}
